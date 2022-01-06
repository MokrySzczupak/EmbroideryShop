package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final int PAGE_SIZE = 10;

    public List<Product> getAllProducts(int pageNumber, SortCriteria sortCriteria) {
        return productRepository.findAllProducts(PageRequest.of(pageNumber, PAGE_SIZE,
                Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString())));
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

    public Product addProduct(Product product) {
        setProperProductCategory(product);
        return productRepository.save(product);
    }

    private void setProperProductCategory(Product product) {
        Category category = categoryRepository.findByName(product.getCategory().getName());
        if (category != null) {
            product.setCategory(category);
        }
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).get();
    }

    public Category addCategory(Category category) {
        if (categoryExists(category)) throw new CategoryAlreadyExistsException(category.getName());
        return categoryRepository.save(category);
    }

    private boolean categoryExists(Category category) {
        Category categoryFromRepo = categoryRepository.findByName(category.getName());
        return categoryFromRepo != null;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Product> getProductsWithCategory(String name, int pageNumber, SortCriteria sortCriteria) {
        long categoryId = categoryRepository
                .findByNameIgnoreCase(name)
                .getCategoryId();
        return productRepository
                .findAllByCategory_CategoryId(categoryId,
                        PageRequest.of(pageNumber, PAGE_SIZE,
                                Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString())));
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public Product editProduct(Product product) {
        Product productEdited = productRepository.findById(product.getId()).orElseThrow();
        productEdited.setName(product.getName());
        productEdited.setPrice(product.getPrice());
        productEdited.setDescription(product.getDescription());
        productEdited.setCategory(product.getCategory());
        return productEdited;
    }

    public Category editCategory(Category category) {
        if (categoryExists(category)) {
            throw new CategoryAlreadyExistsException(category.getName());
        }
        Category categoryEdited = categoryRepository.findById(category.getCategoryId()).orElseThrow();
        categoryEdited.setName(category.getName());
        return categoryEdited;
    }

    public void deleteCategory(long id) {
        if (productWithCategoryExists(id)) {
            throw new CategoryInUseException();
        }
        categoryRepository.deleteById(id);
    }

    public boolean productWithCategoryExists(long id) {
        List<Product> productsWithCategory = productRepository.findAllByCategory_CategoryId(id, PageRequest.of(1, PAGE_SIZE));
        if (!productsWithCategory.isEmpty()) {
            return true;
        }
        return false;
    }
}
