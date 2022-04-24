package com.example.embroideryshop.Payments;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.*;
import com.example.embroideryshop.service.CartService;
import com.example.embroideryshop.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.example.embroideryshop.TestsHelperMethods.createTestCategory;
import static com.example.embroideryshop.TestsHelperMethods.createTestProduct;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin@gmail.com", password = "test", authorities = "ADMIN")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;
    @Value("${Stripe.apiKey}")
    String stripeApiKey;

    @BeforeEach
    public void cleanCartRelatedData() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldCreatePaymentIntent() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartService.getCartForUser(user);
        // when
        paymentService.createPaymentIntent(user);
        // then
        Cart updatedCart = cartService.getCartForUser(user);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(updatedCart.getPaymentId());
        assertThat(paymentIntent).isNotNull();
        assertThat(paymentIntent.getStatus()).isEqualTo("requires_payment_method");
    }

    @Test
    public void shouldCancelPreviousPaymentAfterCreatingNew() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = getCartWithCreatedPaymentForUser(user);
        String previousPaymentId = cart.getPaymentId();
        // when
        paymentService.createPaymentIntent(user);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(previousPaymentId);
        // then
        assertThat(paymentIntent.getStatus()).isEqualTo("canceled");
    }

    @Test
    public void shouldUpdatePaymentIntentAmountAfterAddingNewProductToCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = getCartWithCreatedPaymentForUser(user);
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        productRepository.save(product);
        Stripe.apiKey = stripeApiKey;
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        // when
        cartService.addProduct(product.getId(), 1, user);
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isGreaterThan(oldAmount);
    }

    private Cart getCartWithCreatedPaymentForUser(User user) throws StripeException {
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        return cartService.getCartForUser(user);
    }

    @Test
    public void shouldUpdatePaymentIntentAmountAfterUpdatingProductQuantityInCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = getCartWithCreatedPaymentForUser(user);
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        productRepository.save(product);
        Stripe.apiKey = stripeApiKey;
        // when
        cartService.addProduct(product.getId(), 1, user);
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        cartService.updateQuantity(product.getId(), 2, user);
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isGreaterThan(oldAmount);
    }

    @Test
    public void shouldUpdatePaymentIntentAmountAfterRemovingProductFromCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = getCartWithCreatedPaymentForUser(user);
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        productRepository.save(product);
        // when
        cartService.addProduct(product.getId(), 1, user);
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        cartService.removeProduct(user, product.getId());
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isLessThan(oldAmount);
    }

    @Test
    public void shouldSuccessfullyFinalizePayment() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        productRepository.save(product);
        Stripe.apiKey = stripeApiKey;
        // when
        cartService.addProduct(product.getId(), 1, user);
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        Cart updatedCart = cartService.getCartForUser(user);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(updatedCart.getPaymentId());
        paymentIntent.update(Map.of("payment_method_types", List.of(SessionCreateParams.PaymentMethodType.CARD)));
        PaymentIntent updatedPaymentIntent = paymentIntent.confirm(
                Map.of("payment_method", "pm_card_visa",
                        "return_url", "https://google.com"));
        // then
        assertThat(updatedPaymentIntent.getStatus()).isEqualTo("succeeded");
    }
}
