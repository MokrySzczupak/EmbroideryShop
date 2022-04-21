package com.example.embroideryshop.service;

import com.example.embroideryshop.controller.dto.CartPaginationDto;
import com.example.embroideryshop.exception.EmptyCartException;
import com.example.embroideryshop.exception.NoSuchCartItemException;
import com.example.embroideryshop.exception.WrongQuantityException;
import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
import com.example.embroideryshop.repository.CartRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;
    @Value("${Stripe.apiKey}")
    String stripeApiKey;

    private final int PAGE_SIZE = 2;

    @Transactional
    public void addProduct(long productId, int quantity, User user) throws StripeException {
        if (quantity <= 0) {
            throw new WrongQuantityException();
        }
        updateStatusAndTryToFinalize(user);
        Cart cart = getCartForUser(user);
        Product product = productService.getProductById(productId);
        CartItem cartItem = Optional.ofNullable(cartItemRepository.findByUserAndProduct(user.getId(), productId))
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setProduct(product);
                    newCartItem.setCart(cart);
                    newCartItem.setUser(user);
                    return cartItemRepository.save(newCartItem);
                });
        quantity = cartItem.getQuantity() + quantity;
        cartItem.setQuantity(quantity);
        cart.getCartItems().add(cartItem);
        updatePaymentAmount(cart);
    }

    private void updatePaymentAmount(Cart cart) throws StripeException {
        if (cart.getPaymentId() == null) return;
        Stripe.apiKey = stripeApiKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(cart.getPaymentId());
        long price = cart.calculateCartItemsPrice();
        paymentIntent.update(Map.of("amount", price));
    }

    @Transactional
    public void updateQuantity(long productId, int quantity, User user) throws StripeException {
        if (quantity <= 0) {
            removeProduct(user, productId);
            return;
        }
        updateStatusAndTryToFinalize(user);
        CartItem cartItem = Optional.ofNullable(cartItemRepository.findByUserAndProduct(user.getId(), productId))
                .orElseThrow(NoSuchCartItemException::new);
        cartItem.setQuantity(quantity);
        updatePaymentAmount(cartItem.getCart());
    }

    public void removeProduct(User user, long productId) throws StripeException {
        updateStatusAndTryToFinalize(user);
        CartItem cartItem = Optional.ofNullable(cartItemRepository.findByUserAndProduct(user.getId(), productId))
                .orElseThrow(NoSuchCartItemException::new);
        cartItemRepository.removeByUserAndProduct(user.getId(), productId);
        cartItem.getCart().getCartItems().remove(cartItem);
        updatePaymentAmount(cartItem.getCart());
    }

    public Cart createCartForUser(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        newCart.setStatus("requires_payment_method");
        return cartRepository.save(newCart);
    }

    public CartPaginationDto getAllCarts(int pageNumber, String sortDirection) throws StripeException {
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection.toUpperCase()), "id" );
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, sort);
        List<Cart> carts = cartRepository.getAllCarts(pageable);
        finalizeCartsIfNeeded(carts);
        List<Cart> finalizedCarts = getFinalizedCarts(carts);
        int totalCarts = cartRepository.countFinalizedCarts();
        return createCartPaginationDto(finalizedCarts, pageNumber, totalCarts);
    }

    private List<Cart> getFinalizedCarts(List<Cart> carts) {
        return carts.stream()
                .filter(cart -> !"requires_payment_method".equals(cart.getStatus()))
                .collect(Collectors.toList());
    }

    private void finalizeCartsIfNeeded(List<Cart> carts) throws StripeException {
        for (Cart cart: carts) {
            if (!cart.isPaid() && cart.getPaymentId() != null && cart.getCartItems().size() != 0) {
                updateStatusAndTryToFinalize(cart.getUser());
            }
        }
    }

    private CartPaginationDto createCartPaginationDto(List<Cart> carts, int currentPage, int totalCarts) {
        int totalPages = totalCarts / PAGE_SIZE + ((totalCarts % PAGE_SIZE == 0) ? 0 : 1);
        return new CartPaginationDto(carts, totalCarts, totalPages, currentPage + 1);
    }

    public void setCartCompleted(Long id) {
        cartRepository.setCartCompleted(id);
    }

    public Cart getCartById(Long id) {
        return cartRepository.getSingleCartById(id);
    }

    @Transactional
    public void updateStatusAndTryToFinalize(User user) throws StripeException {
        Cart cart = cartRepository.getCartByUser(user.getId());
        if (cart == null || cart.getPaymentId() == null) return;

        Stripe.apiKey = stripeApiKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(cart.getPaymentId());
        cart.setStatus(paymentIntent.getStatus());
        manageCartWithPaymentIntent(cart, paymentIntent);
    }

    private void manageCartWithPaymentIntent(Cart cart, PaymentIntent paymentIntent) {
        if (!"requires_payment_method".equals(paymentIntent.getStatus())) {
            cart.setCartItemsAsSold();
        }
        if ("succeeded".equals(paymentIntent.getStatus())) {
            cart.setPaid(true);
        }
    }

    public List<CartItem> getCartItemsForUser(User user) throws StripeException {
        updateStatusAndTryToFinalize(user);
        Cart cart = getCartForUser(user);
        return cart.getCartItems();
    }

    public Cart getCartForUser(User user) {
        return Optional.ofNullable(cartRepository.getCartByUser(user.getId()))
                .orElseGet(() -> createCartForUser(user));
    }

    public Cart getFilledCartForUser(User user) {
        return Optional.ofNullable(cartRepository.getCartByUser(user.getId()))
                .orElseThrow(EmptyCartException::new);
    }

    public List<Cart> getAllCartsForUser(User user) throws StripeException {
        updateStatusAndTryToFinalize(user);
        return cartRepository.getAllCartsForUser(user.getId());
    }
}
