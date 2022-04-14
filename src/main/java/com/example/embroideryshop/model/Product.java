package com.example.embroideryshop.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String mainImageName;
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;
}
