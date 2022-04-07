package com.example.embroideryshop.service;

import com.example.embroideryshop.exception.EmptyCartException;
import com.example.embroideryshop.exception.NoSuchCartItemException;
import com.example.embroideryshop.exception.WrongQuantityException;
import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
import com.example.embroideryshop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public List<CartItem> listCartItemsByUser(User user) {
        return cartItemRepository.findByUser(user);
    }

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
        return cartRepository.save(newCart);
    }

    public List<Cart> getAllCarts() {
        return cartRepository.getAllCarts();
    }

    public void setCartCompleted(Long id) {
        cartRepository.setCartCompleted(id);
    }

    public Cart getCartById(Long id) {
        return cartRepository.getSingleCartById(id);
    }

    @Transactional
    public void finalizeCart(User user) {
        Cart cart = cartRepository.getCartByUser(user.getId());
        cart.getCartItems().forEach((cartItem) -> cartItem.setSold(true));
        cart.setPaid(true);
    }

    public List<CartItem> getCartItemsForUser(User user) {
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
}
