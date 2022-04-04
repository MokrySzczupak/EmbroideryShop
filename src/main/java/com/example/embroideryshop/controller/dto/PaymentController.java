package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.CartService;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.ShippingRate;
import com.stripe.model.checkout.Session;
import com.stripe.param.OrderCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.ShippingRateCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${Stripe.apiKey}")
    String stripeApiKey;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/create")
    public String createPaymentIntent(Authentication auth) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        User user = userDetailsService.loadLoggedUser(auth);
        List<CartItem> cartItems = cartService.getCartItemsForUser(user);
        BigDecimal price = BigDecimal.ZERO;
        for (CartItem cartItem: cartItems) {
            price = price.add(cartItem.getSubtotal());
        }
        price = price.setScale(2, RoundingMode.HALF_UP);
        String priceStr = price.toPlainString().replaceAll("\\.", "");
        Long priceL = Long.parseLong(priceStr);
        PaymentIntentCreateParams params1 = PaymentIntentCreateParams.builder()
                .setAmount(priceL)
                .setCurrency("pln")
                .addAllPaymentMethodType(List.of("card", "p24"))
                .setDescription("Płatność użytkownika: " + user.getEmail())
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params1);
        return paymentIntent.getClientSecret();
    }
}
