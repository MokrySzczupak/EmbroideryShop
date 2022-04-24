package com.example.embroideryshop;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TestsHelperMethods {
    public static final String TEST_FILE_NAME = "java-logo.png";
    public static final String TEST_CATEGORY_NAME = "Test category";
    public static final String TEST_PRODUCT_NAME = "Poduszka spersonalizowana";
    public static final String DEFAULT_FILE_NAME = "java-logo.png";

    public static Product createTestProduct() {
        Product product = new Product();
        product.setName(TEST_PRODUCT_NAME);
        product.setDescription("Produkt zawiera puch");
        product.setPrice(BigDecimal.valueOf(55.0));
        product.setMainImageName(TEST_FILE_NAME);
        return product;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setName(TEST_CATEGORY_NAME);
        return category;
    }

    public static Cart createTestCart(User user) {
        Cart cart = new Cart();
        cart.setCartItems(new ArrayList<>());
        cart.setStatus("requires_payment_method");
        cart.setUser(user);
        cart.setPaid(false);
        cart.setCompleted(false);
        return cart;
    }

    public static User createTestUser() {
        User user = new User();
        user.setEmail("testUser@test.test");
        user.setUsername("testUser");
        user.setPassword("testUser");
        return user;
    }
}
