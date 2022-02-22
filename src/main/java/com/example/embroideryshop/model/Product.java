package com.example.embroideryshop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private double price;
    private String mainImageName;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;
}
