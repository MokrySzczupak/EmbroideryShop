package com.example.embroideryshop.service;

import com.example.embroideryshop.controller.dto.CartPaginationDto;
import com.example.embroideryshop.exception.CartNotPaidException;
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
    public void addProduct(long productId, int quantity, User user) {
        if (quantity <= 0) {
            throw new WrongQuantityException();
        }
        Cart cart = Optional.ofNullable(cartRepository.getCartByUser(user.getId()))
                .orElseGet(() -> createCartForUser(user));
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
    }

    @Transactional
    public void updateQuantity(long productId, int quantity, long userId) {
        if (quantity <= 0) {
            removeProduct(userId, productId);
            return;
        }
        CartItem cartItem = Optional.ofNullable(cartItemRepository.findByUserAndProduct(userId, productId))
                .orElseThrow(NoSuchCartItemException::new);
        cartItem.setQuantity(quantity);

    }

    public void removeProduct(long userId, long productId) {
        cartItemRepository.removeByUserAndProduct(userId, productId);
    }

    public Cart createCartForUser(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        newCart.setStatus("requires_payment_method");
        return cartRepository.save(newCart);
    }

    public CartPaginationDto getAllCarts(int pageNumber, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection.toUpperCase()), "id" );
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, sort);
        List<Cart> carts = cartRepository.getAllCarts(pageable);
        finalizeCartsIfNeeded(carts);
        List<Cart> finalizedCarts = removeNonFinalizedCarts(carts);
        int totalCarts = cartRepository.countFinalizedCarts();
        return createCartPaginationDto(finalizedCarts, pageNumber, totalCarts);
    }

    private List<Cart> removeNonFinalizedCarts(List<Cart> carts) {
        return carts.stream()
                .filter(cart -> !"requires_payment_method".equals(cart.getStatus()))
                .collect(Collectors.toList());
    }

    private void finalizeCartsIfNeeded(List<Cart> carts) {
        for (Cart cart: carts) {
            if (!cart.isPaid() && cart.getPaymentId() != null && cart.getCartItems().size() != 0) {
                tryToFinalizeCart(cart.getUser());
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
    public void finalizeCart(User user) throws StripeException {
        Cart cart = cartRepository.getCartByUser(user.getId());
        checkCartPaymentStatus(cart);
        cart.getCartItems().forEach((cartItem) -> cartItem.setSold(true));
        cart.setPaid(true);
    }

    private void checkCartPaymentStatus(Cart cart) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(cart.getPaymentId());
        cart.setStatus(paymentIntent.getStatus());
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new CartNotPaidException();
        }
    }

    public List<CartItem> getCartItemsForUser(User user) {
        tryToFinalizeCart(user);
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

    public List<Cart> getAllCartsForUser(User user) {
        tryToFinalizeCart(user);
        return cartRepository.getAllCartsForUser(user.getId());
    }

    private void tryToFinalizeCart(User user) {
        Cart cart = cartRepository.getCartByUser(user.getId());
        if (cart != null && cart.getPaymentId() != null) {
            try {
                finalizeCart(user);
            } catch (Exception ignored) { }
        }
    }
}
