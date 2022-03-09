package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProductPaginationDto {
    private List<Product> products;
    private int totalProducts;
    private int totalPages;
    private int currentPage;
}
