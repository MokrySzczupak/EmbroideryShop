package com.example.embroideryshop.controller;

import com.example.embroideryshop.controller.dto.CartPaginationDto;
import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.CartService;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        cartService.removeProduct(user.getId(), productId);
    }

    @PostMapping("/finalize")
    public void finalizeCart(Authentication auth) throws StripeException {
        User user = userDetailsService.loadLoggedUser(auth);
        cartService.finalizeCart(user);
    }

    @PostMapping("/complete/{id}")
    public void completeCart(@PathVariable Long id) {
        cartService.setCartCompleted(id);
    }

    @GetMapping("all")
    public CartPaginationDto getAllCarts(@RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortDirection = ("asc".equalsIgnoreCase(sort) || "desc".equalsIgnoreCase(sort)) ? sort : "desc";
        return cartService.getAllCarts(pageNumber, sortDirection);
    }

    @GetMapping("{id}")
    public Cart getCartById(@PathVariable Long id) {
        return cartService.getCartById(id);
    }

    @GetMapping("/all/user")
    public List<Cart> getAllCartsForUser(Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        return cartService.getAllCartsForUser(user);
    }
}
