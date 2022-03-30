package com.example.embroideryshop.controller;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.CartService;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("")
    public List<CartItem> showCart(Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        return cartService.getCartItemsForUser(user);
    }

    @PostMapping("/add/{pid}/{qty}")
    public void addProductToCart(@PathVariable("pid") Long productId,
                                   @PathVariable("qty") Integer quantity,
                                   Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        cartService.addProduct(productId, quantity, user);
    }

    @PutMapping("/update/{pid}/{qty}")
    public void updateQuantity(@PathVariable("pid") Long productId,
                                 @PathVariable("qty") Integer quantity,
                                 Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        cartService.updateQuantity(productId, quantity, user.getId());
    }

    @DeleteMapping("/remove/{pid}")
    public void removeProductFromCart(@PathVariable("pid") Long productId,
                                 Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        cartService.removeProduct(productId, user.getId());
    }

    @PostMapping("/finalize")
    public void finalizeCart(Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        cartService.finalizeCart(user);
    }

    @PostMapping("/complete/{id}")
    public void completeCart(@PathVariable Long id) {
        cartService.setCartCompleted(id);
    }

    @GetMapping("all")
    public List<Cart> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("{id}")
    public Cart getCartById(@PathVariable Long id) {
        return cartService.getCartById(id);
    }
}
