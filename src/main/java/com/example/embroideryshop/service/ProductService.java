package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final int PAGE_SIZE = 10;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsWithName(String name, int pageNumber, SortCriteria sortCriteria) {
        name = formatName(name);
        return productRepository.findAllByNameLikeIgnoreCase(name,
                PageRequest.of(pageNumber, PAGE_SIZE,
                        Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString()))
        );
    }

    private String formatName(String name) {
        return "%" + name.toLowerCase() + "%";
    }
}
