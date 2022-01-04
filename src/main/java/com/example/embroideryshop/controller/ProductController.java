package com.example.embroideryshop.controller;

import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.service.ProductService;
import com.example.embroideryshop.service.SortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    /***
     *
     * @param sort template: property-sortDirection
     */
    @GetMapping("/products/{name}")
    public List<Product> getProductsByName(@PathVariable String name,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getProductsWithName(name, pageNumber, sortCriteria);
    }
}
