package com.example.embroideryshop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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

    private String clientSecret;

    public void setClientSecret(String clientSecret) throws StripeException {
        if (this.clientSecret == null) {
            this.clientSecret = clientSecret;
            return;
        }
        PaymentIntent paymentMethod = PaymentIntent.retrieve(this.clientSecret);
        String status = paymentMethod.getStatus();
        switch(status) {
            case "succeeded":
            case "requires_confirmation":
            case "requires_action":
            case "processing":
            case "requires_payment_method":
                break;
            default:
                this.clientSecret = clientSecret;
                break;
        }
    }
}
