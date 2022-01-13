package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
import com.example.embroideryshop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    public void addProduct(long productId, int quantity, User user) {
        Product product = productService.getProductById(productId);
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
        if (cartItem != null && !cartItem.isSold()) {
            quantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setUser(user);
            cartItem.setQuantity(quantity);
            cartItem.setSold(false);
        }
        cartItemRepository.save(cartItem);
    }

    public void updateQuantity(long productId, int quantity, long userId) {
        cartItemRepository.updateQuantity(quantity, productId, userId);
    }

    public void removeProduct(long productId, User user) {
        cartItemRepository.removeByUserAndProduct(user.getId(), productId);
    }

    @Transactional
    public Cart createCart(User user, List<CartItem> cartItems) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalPrice(0);
        Cart cart = cartRepository.save(newCart);
        for (int i = 0; i < cartItems.size(); i++) {
            cartItems.get(i).setCart(cart);
            System.out.println("Cart id: " + cart.getId());
            System.out.println("Item: " + cartItems.get(i).getId() + " fk " + cartItems.get(i).getCart().getId());
            cartItems.get(i).setSold(true);
//            cartItemRepository.save(cartItems.get(i));
        }
        return cart;
    }

    public List<Cart> getALlCarts() {
        return cartRepository.getAllCarts();
    }
}
