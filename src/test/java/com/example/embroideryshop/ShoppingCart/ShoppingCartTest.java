package com.example.embroideryshop.ShoppingCart;

import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

        assertThat(savedCartItem.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldGetCartItemsByUser() {
        User user = new User();
        user.setId(2);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        assertThat(cartItems.size()).isGreaterThan(0);
    }

    @Test
    @Transactional
    public void shouldDeleteCartItem() {
        User user = new User();
        user.setId(2);
        Product product = entityManager.find(Product.class, 20L);
        cartItemRepository.removeByUserAndProduct(user.getId(), product.getId());
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
        assertThat(cartItem).isNull();
    }

    @Test
    @Transactional
    public void shouldUpdateQuantity() {
        Product product = entityManager.find(Product.class, 1L);
        User user = entityManager.find(User.class, 2L);
        cartItemRepository.updateQuantity(10, 1L, 2);
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
        assertThat(cartItem.getQuantity()).isEqualTo(10);
    }

    @Test
    public void shouldGetCartItemByUserAndProduct() {
        Product product = entityManager.find(Product.class, 1L);
        User user = entityManager.find(User.class, 2L);
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
        assertThat(cartItem).isNotNull();
    }

}
