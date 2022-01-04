package com.example.embroideryshop.service;

public enum ProductProperty {
    ID, NAME, PRICE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static ProductProperty fromString(String property) {
        return ProductProperty.valueOf(property.toUpperCase());
    }
}
