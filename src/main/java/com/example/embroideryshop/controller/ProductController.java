package com.example.embroideryshop.controller;

import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.service.ProductService;
import com.example.embroideryshop.service.SortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getAllProducts(pageNumber, sortCriteria);
    }

    /***
     *
     * @param sort template: property-sortDirection
     */
    @GetMapping("/products/search/{name}")
    public List<Product> getProductsByName(@PathVariable String name,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getProductsWithName(name, pageNumber, sortCriteria);
    }

    @GetMapping("/products/{id}")
    public Product getSingleProduct(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product,
                              @RequestParam("file") MultipartFile multipartFile) throws IOException {
        return productService.addProduct(product, multipartFile);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
    }

    @PutMapping("/products")
    public Product editProduct(@RequestBody Product product) {
        return productService.editProduct(product);
    }

    @PostMapping("/products/category")
    public Category addCategory(@RequestBody Category category) {
        return productService.addCategory(category);
    }

    @GetMapping("/products/category")
    public List<Category> getAllCategories() {
        return productService.getAllCategories();
    }

    @GetMapping("/products/category/{name}")
    public List<Product> getProductsWithCategory(@PathVariable String name,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getProductsWithCategory(name, pageNumber, sortCriteria);
    }

    @PutMapping("/products/category")
    public Category editCategory(@RequestBody Category category) {
        return productService.editCategory(category);
    }

    @DeleteMapping("/products/category/{id}")
    public void deleteCategory(@PathVariable long id) {
        productService.deleteCategory(id);
    }
}
