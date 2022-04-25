package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PaymentService {

    @Value("${Stripe.apiKey}")
    String stripeApiKey;
    @Autowired
    private CartService cartService;

    @Transactional
    public String createPaymentIntent(User user) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Cart cart = cartService.getFilledCartForUser(user);
        PaymentIntentCreateParams params = createPaymentParams(user, cart);
        PaymentIntent paymentIntent = cart.createPaymentIntent(params);
        return paymentIntent.getClientSecret();
    }

    private PaymentIntentCreateParams createPaymentParams(User user, Cart cart) {
        return PaymentIntentCreateParams.builder()
                .setAmount(cart.calculateCartItemsPrice())
                .setCurrency("pln")
                .addAllPaymentMethodType(List.of("p24"))
                .setDescription("Płatność użytkownika: " + user.getEmail())
                .build();
    }
}
