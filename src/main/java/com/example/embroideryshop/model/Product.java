package com.example.embroideryshop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private double price;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;
}
