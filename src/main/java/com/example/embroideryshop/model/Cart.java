package com.example.embroideryshop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Transient
    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem cartItem: cartItems) {
            total += cartItem.getSubtotal();
        }
        return total;
    }
}
