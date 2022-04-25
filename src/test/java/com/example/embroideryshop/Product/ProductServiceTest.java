package com.example.embroideryshop.Product;

import com.example.embroideryshop.exception.CategoryAlreadyExistsException;
import com.example.embroideryshop.exception.CategoryInUseException;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CartItemRepository;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.repository.ProductRepository;
import com.example.embroideryshop.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.example.embroideryshop.TestsHelperMethods.*;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private final int DEFAULT_PAGE = 0;
    private final SortCriteria DEFAULT_SORT_CRITERIA = new SortCriteria(Sort.Direction.ASC, ProductProperty.ID);

    @BeforeEach
    public void cleanTestData() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldGetSingleProduct() {
        // given
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        productRepository.save(product);
        // when
        Product singleProduct = productService.getProductById(product.getId());
        // then
        assertThat(singleProduct.getId()).isEqualTo(product.getId());
    }

    @Test
    public void shouldGetListOfProducts() {
        // given
        Category category = createTestCategory();
        categoryRepository.save(category);
        Product firstProduct = createTestProduct();
        firstProduct.setCategory(category);
        Product secondProduct = createTestProduct();
        secondProduct.setCategory(category);
        Product thirdProduct = createTestProduct();
        thirdProduct.setCategory(category);
        productRepository.saveAll(List.of(firstProduct, secondProduct, thirdProduct));
        // when
        List<Product> products = productService.getAllProducts(DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        // then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(3);
        assertThat(products.get(1)).isInstanceOf(Product.class);
    }

    @Test
    public void shouldEditProduct() {
        // given
        Product productToEdit = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        productToEdit.setCategory(category);
        productRepository.save(productToEdit);
        // when
        Product editedProduct = createEditedProduct(productToEdit.getId());
        Product editedProductResult = productService.editProduct(editedProduct);
        // then
        assertThat(editedProductResult).isNotNull();
        assertThat(editedProductResult.getId()).isEqualTo(productToEdit.getId());
        assertThat(editedProductResult.getName()).isEqualTo("productEdited");
        assertThat(editedProductResult.getPrice()).isEqualTo(BigDecimal.valueOf(22.22));
        assertThat(editedProductResult.getMainImageName()).isEqualTo("java-logoEdited.png");
        assertThat(editedProductResult.getCategory().getName()).isEqualTo("New category");
    }

    private Product createEditedProduct(long editProductId) {
        Product editedProduct = new Product();
        editedProduct.setId(editProductId);
        editedProduct.setMainImageName("java-logoEdited.png");
        editedProduct.setName("productEdited");
        Category category = new Category();
        category.setName("New category");
        editedProduct.setCategory(categoryRepository.save(category));
        editedProduct.setPrice(BigDecimal.valueOf(22.22));
        return editedProduct;
    }

    @Test
    public void shouldGetProductsByName() {
        // given
        Category category = createTestCategory();
        categoryRepository.save(category);
        Product firstProduct = createTestProduct();
        firstProduct.setCategory(category);
        Product secondProduct = createTestProduct();
        secondProduct.setCategory(category);
        Product thirdProduct = createTestProduct();
        thirdProduct.setCategory(category);
        thirdProduct.setName("Should not be found");
        productRepository.saveAll(List.of(firstProduct, secondProduct, thirdProduct));
        // when
        List<Product> products = productService.getProductsWithName(TEST_PRODUCT_NAME, DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        // then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        for (Product product: products) {
            assertThat(product.getName()).contains(TEST_PRODUCT_NAME);
        }
    }

    @Test
    public void shouldAddProduct() throws IOException {
        // given
        Product product = createTestProduct();
        product.setCategory(categoryRepository.save(createTestCategory()));
        MultipartFile multipartFile = createTestMultipartFile();
        // when
        Product addedProduct = productService.addProduct(product, product.getCategory().getName(), multipartFile);
        // then
        assertThat(product.getName()).isEqualTo(TEST_PRODUCT_NAME);
        assertThat(addedProduct.getCategory().getName()).isEqualTo(TEST_CATEGORY_NAME);
        assertThat(addedProduct.getMainImageName()).isEqualTo(DEFAULT_FILE_NAME);
    }

    public MultipartFile createTestMultipartFile() throws IOException {
        FileInputStream fis = new FileInputStream("./src/test/resources/" + DEFAULT_FILE_NAME);
        return new MockMultipartFile("file", DEFAULT_FILE_NAME, "multipart/form-data", fis);
    }

    @Test
    public void shouldAddCategory() {
        // given
        Category newCategory = createTestCategory();
        // when
        Category category = productService.addCategory(newCategory);
        // then
        assertThat(category.getName()).isEqualTo(TEST_CATEGORY_NAME);
    }

    @Test
    public void shouldThrowCategoryAlreadyExistsException() {
        Category category = createTestCategory();
        categoryRepository.save(category);
        Category duplicateCategory = createTestCategory();

        assertThatThrownBy(() -> productService.addCategory(duplicateCategory))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Kategoria '" + duplicateCategory.getName() + "' już istnieje");
    }

    @Test
    public void shouldThrowCategoryInUseException() {
        Product product = createTestProduct();
        Category categoryInUse = createTestCategory();
        product.setCategory(categoryRepository.save(categoryInUse));
        productRepository.save(product);

        assertThatThrownBy(() -> productService.deleteCategory(categoryInUse.getCategoryId()))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessageContaining("Kategoria jest obecnie wykorzystywana przed inne produkty.");
    }

    @Test
    public void shouldGetProductsWithCategory() {
        // given
        List<Product> testProductsWithSameCategory = createListOfProductsWithAttachedCategory();
        productRepository.saveAll(testProductsWithSameCategory);
        Category category = testProductsWithSameCategory.get(0).getCategory();
        Product productWithWrongCategory = createTestProduct();
        Category wrongCategory = createTestCategory();
        wrongCategory.setName("Wrong category");
        productWithWrongCategory.setCategory(categoryRepository.save(wrongCategory));
        productRepository.save(productWithWrongCategory);
        // when
        List<Product> products = productService.getProductsWithCategory(category.getName(),
                DEFAULT_PAGE, DEFAULT_SORT_CRITERIA).getProducts();
        // then
        assertThat(products).hasSize(testProductsWithSameCategory.size());
        for (Product product: products) {
            assertThat(product.getCategory()).isEqualTo(category);
        }
    }

    private List<Product> createListOfProductsWithAttachedCategory() {
        Category category = createTestCategory();
        categoryRepository.save(category);
        Product firstProduct = createTestProduct();
        firstProduct.setCategory(category);
        Product secondProduct = createTestProduct();
        secondProduct.setCategory(category);
        Product thirdProduct = createTestProduct();
        thirdProduct.setCategory(category);
        return List.of(firstProduct, secondProduct, thirdProduct);
    }

    @Test
    public void shouldEditCategory() {
        // given
        Category categoryToEdit = categoryRepository.save(createTestCategory());
        long categoryToEditId = categoryToEdit.getCategoryId();
        categoryToEdit.setName("editedCategory");
        // when
        Category category = productService.editCategory(categoryToEdit);
        // then
        assertThat(category.getCategoryId()).isEqualTo(categoryToEditId);
        assertThat(category.getName()).isEqualTo("editedCategory");
    }

}