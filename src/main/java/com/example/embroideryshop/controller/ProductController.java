package com.example.embroideryshop.controller;

import com.example.embroideryshop.controller.dto.ProductPaginationDto;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.service.ProductService;
import com.example.embroideryshop.service.SortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ProductPaginationDto getProducts(@RequestParam(required = false) Integer page,
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
    @GetMapping("/search/{name}")
    public ProductPaginationDto getProductsByName(@PathVariable String name,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getProductsWithName(name, pageNumber, sortCriteria);
    }

    @GetMapping("{id}")
    public Product getSingleProduct(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Product addProduct(@RequestPart String product, @RequestPart String category,
                              @RequestPart MultipartFile multipartFile) throws IOException {
        Product productJson = productService.getJson(product);
        return productService.addProduct(productJson, category, multipartFile);
    }

    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
    }

    @PutMapping("")
    public Product editProduct(@RequestBody Product product) {
        return productService.editProduct(product);
    }

    @PostMapping("/category")
    public Category addCategory(@RequestBody Category category) {
        return productService.addCategory(category);
    }

    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return productService.getAllCategories();
    }

    @GetMapping("/category/{name}")
    public ProductPaginationDto getProductsWithCategory(@PathVariable String name,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) String sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        String sortQuery = sort != null ? sort : "desc-id";
        SortCriteria sortCriteria = SortCriteria.fromQuery(sortQuery);
        return productService.getProductsWithCategory(name, pageNumber, sortCriteria);
    }

    @PutMapping("/category")
    public Category editCategory(@RequestBody Category category) {
        return productService.editCategory(category);
    }

    @DeleteMapping("/category/{id}")
    public void deleteCategory(@PathVariable long id) {
        productService.deleteCategory(id);
    }

}
