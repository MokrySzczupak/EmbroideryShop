package com.example.embroideryshop.Payments;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.UserRepository;
import com.example.embroideryshop.service.CartService;
import com.example.embroideryshop.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin@gmail.com", password = "test", authorities = "ADMIN")
public class PaymentsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;
    @Value("${Stripe.apiKey}")
    String stripeApiKey;

    @Test
    @Transactional
    public void shouldCreatePaymentIntent() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        // when
        Cart cart = cartService.getCartForUser(user);
        paymentService.createPaymentIntent(user);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(cart.getPaymentId());
        // then
        assertThat(paymentIntent).isNotNull();
        assertThat(paymentIntent.getStatus()).isEqualTo("requires_payment_method");
    }

    @Test
    @Transactional
    public void shouldCancelPreviousPaymentAfterCreatingNew() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        String previousPaymentId = cart.getPaymentId();
        // when
        paymentService.createPaymentIntent(user);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(previousPaymentId);
        // then
        assertThat(paymentIntent.getStatus()).isEqualTo("canceled");
    }

    @Test
    @Transactional
    public void shouldUpdatePaymentIntentAmountAfterAddingNewProductToCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        Stripe.apiKey = stripeApiKey;
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        // when
        cartService.addProduct(1, 1, user);
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isGreaterThan(oldAmount);
    }

    @Test
    @Transactional
    public void shouldUpdatePaymentIntentAmountAfterUpdatingProductQuantityInCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        Stripe.apiKey = stripeApiKey;
        // when
        cartService.addProduct(1, 1, user);
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        cartService.updateQuantity(1, 2, user);
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isGreaterThan(oldAmount);
    }

    @Test
    @Transactional
    public void shouldUpdatePaymentIntentAmountAfterRemovingProductFromCart() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Cart cart = cartService.getCartForUser(user);
        if (cart.getPaymentId() == null) {
            paymentService.createPaymentIntent(user);
        }
        // when
        cartService.addProduct(1, 1, user);
        Long oldAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        cartService.removeProduct(user, 1);
        // then
        Long updatedAmount = PaymentIntent.retrieve(cart.getPaymentId()).getAmount();
        assertThat(updatedAmount).isLessThan(oldAmount);
    }

    @Test
    @Transactional
    public void shouldSuccessfullyFinalizePayment() throws StripeException {
        // given
        User user = userRepository.findUserByEmail("admin@gmail.com");
        Stripe.apiKey = stripeApiKey;
        // when
        cartService.addProduct(1, 1, user);
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
