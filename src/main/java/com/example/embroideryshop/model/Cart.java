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
        setStatusAndPaymentId(paymentIntent.getStatus(), paymentIntent.getId());
        return paymentIntent;
    }

    private void setStatusAndPaymentId(String status, String paymentId) {
        this.status = status;
        this.paymentId = paymentId;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
        if (paid) {
            setCartItemsAsSold();
        }
    }

    public void setCartItemsAsSold() {
        cartItems.forEach((cartItem) -> cartItem.setSold(true));
    }
}
