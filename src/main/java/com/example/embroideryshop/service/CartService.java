package com.example.embroideryshop.service;

import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
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
    private ProductService productService;

    public List<CartItem> listCartItemsByUser(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addProduct(long productId, int quantity, User user) {
        Product product = productService.getProductById(productId);
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
        if (cartItem != null) {
            quantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setUser(user);
            cartItem.setQuantity(quantity);
        }
        cartItemRepository.save(cartItem);
    }

    public void updateQuantity(long productId, int quantity, User user) {
        cartItemRepository.updateQuantity(quantity, productId, user.getId());
    }

    public void removeProduct(long productId, User user) {
        cartItemRepository.removeByUserAndProduct(user.getId(), productId);
    }
}
