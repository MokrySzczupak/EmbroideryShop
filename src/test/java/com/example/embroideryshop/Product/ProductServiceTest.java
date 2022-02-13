package com.example.embroideryshop.Product;

import com.example.embroideryshop.exception.CategoryAlreadyExistsException;
import com.example.embroideryshop.exception.CategoryInUseException;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    private final int DEFAULT_PAGE = 1;
    private final SortCriteria DEFAULT_SORT_CRITERIA = new SortCriteria(Sort.Direction.ASC, ProductProperty.ID);
    private final String DEFAULT_FILE_NAME = "java-logo.png";

    @Test
    public void shouldGetSingleProduct() {
        Product singleProduct = productService.getProductById(1L);
        assertThat(singleProduct.getId()).isEqualTo(1L);
    }

    @Test
    public void shouldGetListOfProducts() {
        List<Product> products = productService.getAllProducts(DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        assertThat(products).isNotNull();
        assertThat(products).isInstanceOf(products.getClass());
    }

    @Test
    @Transactional
    public void shouldDeleteProduct() {
        productService.deleteProduct(1L);
        Product product = productService.getProductById(1L);
        assertThat(product).isNull();
    }

    @Test
    @Transactional
    public void shouldEditProduct() {
        Product editedProduct = createEditedProduct();
        Product product = productService.editProduct(editedProduct);
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(2);
        assertThat(product.getName()).isEqualTo("productEdited");
        assertThat(product.getPrice()).isEqualTo(22.22);
        assertThat(product.getMainImageName()).isEqualTo("java-logoEdited.png");
        assertThat(product.getCategory()).isEqualTo(categoryRepository.findByName("Category 2"));
    }

    private Product createEditedProduct() {
        Product editedProduct = new Product();
        editedProduct.setId(2);
        editedProduct.setMainImageName("java-logoEdited.png");
        editedProduct.setName("productEdited");
        editedProduct.setCategory(categoryRepository.findByName("Category 2"));
        editedProduct.setPrice(22.22);
        return editedProduct;
    }

    @Test
    public void shouldGetProductsByName() {
        List<Product> products = productService.getProductsWithName("Name 4", DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        assertThat(products).isNotNull();
        for (Product product: products) {
            assertThat(product.getName()).contains("Name 4");
        }
    }

    @Test
    @Transactional
    public void shouldAddProduct() throws IOException {
        Product product = createTestProduct();
        System.out.println("shouldAddProduct: " + product.getCategory());
        MultipartFile multipartFile = createTestMultipartFile();
        Product addedProduct = productService.addProduct(product, multipartFile);
        assertThat(product.getName()).isEqualTo("productName");
        assertThat(addedProduct.getCategory()).isEqualTo(categoryRepository.findByName("Category 2"));
        assertThat(addedProduct.getMainImageName()).isEqualTo(DEFAULT_FILE_NAME);
    }

    public MultipartFile createTestMultipartFile() throws IOException {
        FileInputStream fis = new FileInputStream("./src/test/resources/" + DEFAULT_FILE_NAME);
        return new MockMultipartFile("file", DEFAULT_FILE_NAME, "multipart/form-data", fis);
    }

    public Product createTestProduct() {
        Product product = new Product();
        product.setName("productName");
        product.setPrice(22.22);
        product.setCategory(categoryRepository.findByName("Category 2"));
        return product;
    }

    @Test
    @Transactional
    public void shouldAddCategory() {
        Category newCategory = new Category();
        newCategory.setName("newCategory");
        Category category = productService.addCategory(newCategory);
        assertThat(category.getName()).isEqualTo("newCategory");
    }

    @Test
    @Transactional
    public void shouldThrowCategoryAlreadyExistsException() {
        Category category = categoryRepository.findByName("Category 2");
        assertThatThrownBy(() -> productService.addCategory(category))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Kategoria '" + category.getName() + "' już istnieje");
    }

    @Test
    @Transactional
    public void shouldThrowCategoryInUseException() {
        Category categoryInUse = categoryRepository.findByName("Category 2");
        assertThatThrownBy(() -> productService.deleteCategory(categoryInUse.getCategoryId()))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessageContaining("Kategoria jest obecnie wykorzystywana przed inne produkty.");
    }

    @Test
    @Transactional
    public void shouldGetProductsWithCategory() {
        Category category = categoryRepository.findByName("Category 2");
        List<Product> products = productService.getProductsWithCategory(category.getName(),
                DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        for (Product product: products) {
            assertThat(product.getCategory()).isEqualTo(category);
        }
    }

    @Test
    @Transactional
    public void shouldEditCategory() {
        Category editedCategory = new Category();
        editedCategory.setCategoryId(5);
        editedCategory.setName("editedCategory");
        Category category = productService.editCategory(editedCategory);
        assertThat(category.getCategoryId()).isEqualTo(5);
        assertThat(category.getName()).isEqualTo("editedCategory");
    }

}