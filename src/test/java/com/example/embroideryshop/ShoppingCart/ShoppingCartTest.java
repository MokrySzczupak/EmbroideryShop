package com.example.embroideryshop.ShoppingCart;

import com.example.embroideryshop.model.*;
import com.example.embroideryshop.repository.*;
import com.example.embroideryshop.service.CartService;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static com.example.embroideryshop.TestsHelperMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ShoppingCartTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanTestData() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldAddOneCartItem() throws StripeException {
        // given
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        User user = userRepository.findUserByEmail("admin@gmail.com");
        productRepository.save(product);
        // when
        cartService.addProduct(product.getId(), 1, user);
        // then
        CartItem savedCartItem = cartItemRepository.findByUserAndProduct(user.getId(), product.getId());
        assertThat(savedCartItem).isNotNull();
        assertThat(savedCartItem.getQuantity()).isGreaterThan(0);
        assertThat(savedCartItem.getProduct()).isNotNull();
    }

    @Test
    public void shouldGetCartItemsByUser() {
        // given
        List<CartItem> testCartItems = createListOfTestCartItems();
        cartItemRepository.saveAll(testCartItems);
        User user = testCartItems.get(0).getUser();
        // when
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        // then
        assertThat(cartItems).hasSize(3);
        for (CartItem cartItem: cartItems) {
            assertThat(cartItem.getUser().getEmail()).isEqualTo(user.getEmail());
        }
    }

    @Test
    public void shouldDeleteCartItem() throws StripeException {
        // given
        CartItem cartItemToDelete = createTestCartItem();
        cartItemRepository.save(cartItemToDelete);
        // when
        cartService.removeProduct(cartItemToDelete.getUser(), cartItemToDelete.getProduct().getId());
        // then
        CartItem deletedCartItem = cartItemRepository.findByUserAndProduct(cartItemToDelete.getUser().getId(),
                cartItemToDelete.getProduct().getId());
        assertThat(deletedCartItem).isNull();
    }

    @Test
    public void shouldUpdateQuantity() throws StripeException {
        // given
        CartItem cartItem = createTestCartItem();
        long productId = cartItem.getProduct().getId();
        long userId = cartItem.getUser().getId();
        int quantity = cartItem.getQuantity();
        cartItemRepository.save(cartItem);
        // when
        cartService.updateQuantity( productId, quantity + 2, cartItem.getUser());
        // then
        CartItem updatedCartItem = cartItemRepository.findByUserAndProduct(userId, productId);
        assertThat(updatedCartItem.getQuantity()).isEqualTo(quantity + 2);
    }

    @Test
    public void shouldGetCartItemByUserAndProduct() {
        // given
        CartItem cartItem = createTestCartItem();
        long productId = cartItem.getProduct().getId();
        long userId = cartItem.getUser().getId();
        cartItemRepository.save(cartItem);
        // when
        CartItem foundCartItem = cartItemRepository.findByUserAndProduct(userId, productId);
        // then
        assertThat(foundCartItem).isNotNull();
        assertThat(foundCartItem.getProduct().getId()).isEqualTo(productId);
        assertThat(foundCartItem.getUser().getId()).isEqualTo(userId);
    }

    // private helper methods

    private List<CartItem> createListOfTestCartItems() {
        User user = userRepository.findUserByEmail("admin@gmail.com");
        CartItem cartItemOne = createTestCartItemWithoutCart();
        CartItem cartItemTwo = createTestCartItemWithoutCart();
        CartItem cartItemThree = createTestCartItemWithoutCart();
        Cart cart = createTestCart(user);
        cartRepository.save(cart);
        cartItemOne.setCart(cart);
        cartItemTwo.setCart(cart);
        cartItemThree.setCart(cart);
        return List.of(cartItemOne, cartItemTwo, cartItemThree);
    }

    private CartItem createTestCartItemWithoutCart() {
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        User user = userRepository.findUserByEmail("admin@gmail.com");
        CartItem testCartItem = new CartItem();
        testCartItem.setUser(user);
        testCartItem.setQuantity(1);
        testCartItem.setSold(false);
        testCartItem.setProduct(productRepository.save(product));
        return testCartItem;
    }

    private CartItem createTestCartItem() {
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartRepository.save(createTestCart(user));
        CartItem testCartItem = new CartItem();
        testCartItem.setUser(user);
        testCartItem.setQuantity(1);
        testCartItem.setSold(false);
        testCartItem.setCart(cart);
        testCartItem.setProduct(productRepository.save(product));
        return testCartItem;
    }
}
