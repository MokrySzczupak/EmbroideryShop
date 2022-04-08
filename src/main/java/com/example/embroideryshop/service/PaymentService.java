package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Cart;
import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PaymentService {

    @Value("${Stripe.apiKey}")
    String stripeApiKey;
    @Autowired
    private CartService cartService;
    private final int SHIPPING_PRICE = 15;

    @Transactional
    public String createPaymentIntent(User user) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Cart cart = cartService.getFilledCartForUser(user);
        PaymentIntentCreateParams params = createPaymentParams(user, cart);
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        cart.setPaymentId(paymentIntent.getId());
        return paymentIntent.getClientSecret();
    }

    private PaymentIntentCreateParams createPaymentParams(User user, Cart cart) {
        return PaymentIntentCreateParams.builder()
                .setAmount(calculateCartItemsPrice(cart))
                .setCurrency("pln")
                .addAllPaymentMethodType(List.of("card", "p24"))
                .setDescription("Płatność użytkownika: " + user.getEmail())
                .build();
    }

    private long calculateCartItemsPrice(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        BigDecimal price = BigDecimal.valueOf(SHIPPING_PRICE);
        for (CartItem cartItem: cartItems) {
            price = price.add(cartItem.getSubtotal());
        }
        price = price.setScale(2, RoundingMode.HALF_UP);
        String priceStr = price.toPlainString().replaceAll("\\.", "");
        return Long.parseLong(priceStr);
    }
}
