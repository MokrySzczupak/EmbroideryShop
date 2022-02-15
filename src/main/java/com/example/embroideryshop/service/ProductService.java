package com.example.embroideryshop.service;

import com.example.embroideryshop.controller.dto.ProductPaginationDto;
import com.example.embroideryshop.exception.CategoryAlreadyExistsException;
import com.example.embroideryshop.exception.CategoryInUseException;
import com.example.embroideryshop.exception.NoSuchProductException;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final int PAGE_SIZE = 12;

    public ProductPaginationDto getAllProducts(int pageNumber, SortCriteria sortCriteria) {
        List<Product> products = productRepository.findAllProducts(PageRequest.of(pageNumber, PAGE_SIZE,
                Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString()))
        );
        int totalProducts = productRepository.countProductBy();
        return createProductPaginationDto(products, pageNumber, totalProducts);
    }

    private ProductPaginationDto createProductPaginationDto(List<Product> products, int currentPage, int totalProducts) {
        int totalPages = totalProducts / PAGE_SIZE + ((totalProducts % PAGE_SIZE == 0) ? 0 : 1);
        return new ProductPaginationDto(products, totalProducts, totalPages, currentPage + 1);
    }

    public ProductPaginationDto getProductsWithName(String name, int pageNumber, SortCriteria sortCriteria) {
        name = formatName(name);
        List<Product> products = productRepository.findAllByNameLikeIgnoreCase(name,
                PageRequest.of(pageNumber, PAGE_SIZE,
                        Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString()))
        );
        int totalProducts = productRepository.countProductByNameLikeIgnoreCase(name);
        return createProductPaginationDto(products, pageNumber, totalProducts);
    }

    private String formatName(String name) {
        return "%" + name.toLowerCase() + "%";
    }

    public Product addProduct(Product product, MultipartFile multipartFile) throws IOException {
        setProperProductCategory(product);

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        product.setMainImageName(fileName);
        Product savedProduct = productRepository.save(product);

        String uploadDir = "./src/main/resources/static/mainImages/" + savedProduct.getId();
        Path uploadPath = Paths.get(uploadDir);
        createDirectoriesIfNotExists(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        saveUploadedMultipartFile(multipartFile, filePath);
        return savedProduct;
    }

    private void setProperProductCategory(Product product) {
        Category category = categoryRepository.findByName(product.getCategory().getName());
        product.setCategory(category);
    }

    private void createDirectoriesIfNotExists(Path uploadPath) throws IOException {
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    private void saveUploadedMultipartFile(MultipartFile multipartFile, Path filePath) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Nie udało się zapisać pliku: " + multipartFile.getOriginalFilename());
        }
    }

    public Product getProductById(long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new NoSuchProductException();
        }
        return product;
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

    public ProductPaginationDto getProductsWithCategory(String name, int pageNumber, SortCriteria sortCriteria) {
        Category category = categoryRepository.findByNameIgnoreCase(name);
        if (category == null) {
            throw new NoSuchElementException();
        }
        long categoryId = category.getCategoryId();
        List<Product> products = productRepository
                .findAllByCategory_CategoryId(categoryId,
                        PageRequest.of(pageNumber, PAGE_SIZE,
                                Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString())));
        int totalProducts = productRepository.countProductByCategory_CategoryId(categoryId);
        return createProductPaginationDto(products, pageNumber, totalProducts);
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product editProduct(Product product) {
        Product productEdited = productRepository.findById(product.getId()).orElseThrow();
        productEdited.setName(product.getName());
        productEdited.setPrice(product.getPrice());
        productEdited.setMainImageName(product.getMainImageName());
        productEdited.setDescription(product.getDescription());
        productEdited.setCategory(product.getCategory());
        return productEdited;
    }

    @Transactional
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

    private boolean productWithCategoryExists(long id) {
        List<Product> productsWithCategory = productRepository.findAllByCategory_CategoryId(id, PageRequest.of(1, PAGE_SIZE));
        if (!productsWithCategory.isEmpty()) {
            return true;
        }
        return false;
    }
}