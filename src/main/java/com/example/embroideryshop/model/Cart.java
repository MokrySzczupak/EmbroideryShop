package com.example.embroideryshop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart")
    @JsonManagedReference
    private List<CartItem> cartItems;

    private double totalPrice;

    private boolean completed;

    private boolean paid;

    private String paymentId;

    private String status;

    @Transient
    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem cartItem: cartItems) {
            total += cartItem.getSubtotal().doubleValue();
        }
        return total;
    }

    public PaymentIntent createPaymentIntent(PaymentIntentCreateParams params) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        if (this.paymentId == null) {
            this.status = paymentIntent.getStatus();
            this.paymentId = paymentIntent.getId();
            return paymentIntent;
        }
        PaymentIntent retrievedPayment = PaymentIntent.retrieve(this.paymentId);
        String status = retrievedPayment.getStatus();
        switch(status) {
            case "succeeded":
            case "requires_confirmation":
            case "requires_action":
            case "processing":
            case "requires_payment_method":
                this.status = retrievedPayment.getStatus();
                return retrievedPayment;
            default:
                this.paymentId = paymentIntent.getId();
                this.status = paymentIntent.getStatus();
                return paymentIntent;
        }
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
        if (paid) {
            cartItems.forEach((cartItem) -> cartItem.setSold(true));
        }
    }
}
