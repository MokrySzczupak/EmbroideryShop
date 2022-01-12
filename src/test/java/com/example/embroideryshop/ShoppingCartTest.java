package com.example.embroideryshop;

import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class ShoppingCartTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldAddOneCartItem() {
        Product product = entityManager.find(Product.class, 20L);
        User user = entityManager.find(User.class, 2L);

        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setUser(user);
        newItem.setQuantity(1);

        CartItem savedCartItem = cartItemRepository.save(newItem);

        Assertions.assertTrue(savedCartItem.getId() > 0);
    }

    @Test
    public void shouldGetCartItemsByCustomer() {
        User user = new User();
        user.setId(2);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        Assertions.assertEquals(2, cartItems.size());
    }
}
