package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CartPaginationDto {
    private List<Cart> carts;
    private int totalCarts;
    private int totalPages;
    private int currentPage;
}
