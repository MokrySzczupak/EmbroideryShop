package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.PaymentService;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public String createPaymentIntent(Authentication auth) throws StripeException {
        User user = userDetailsService.loadLoggedUser(auth);
        return paymentService.createPaymentIntent(user);
    }
}
