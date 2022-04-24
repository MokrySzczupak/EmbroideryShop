package com.example.embroideryshop.Product;

import com.example.embroideryshop.controller.dto.ProductPaginationDto;
import com.example.embroideryshop.exception.CategoryAlreadyExistsException;
import com.example.embroideryshop.exception.CategoryInUseException;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CartItemRepository;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

import static com.example.embroideryshop.TestsHelperMethods.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin@gmail.com", password = "test", authorities = "ADMIN")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void cleanTestData() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldAddProduct() throws Exception {
        // given
        MockMultipartFile file = createTestMultipartImageFile();
        Product product = createTestProduct();
        product.setMainImageName(file.getOriginalFilename());
        Category category = createTestCategory();
        categoryRepository.save(category);
        MockMultipartFile jsonProduct = new MockMultipartFile("product", "",
                "application/json", objectMapper.writeValueAsString(product).getBytes());
        MockMultipartFile jsonCategory = new MockMultipartFile("category", "",
                "application/json", TEST_CATEGORY_NAME.getBytes());
        // when
        MvcResult mvcResult = mockMvc.perform(multipart("/products/")
                        .file(file)
                        .file(jsonProduct)
                        .file(jsonCategory))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andReturn();
        // then
        Product addedProduct = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(addedProduct).isNotNull();
        assertThat(addedProduct.getName()).isEqualTo("Poduszka spersonalizowana");
        assertThat(addedProduct.getDescription()).isEqualTo("Produkt zawiera puch");
        assertThat(addedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(55.00));
        assertThat(addedProduct.getCategory().getName()).isEqualTo(TEST_CATEGORY_NAME);
        assertThat(addedProduct.getMainImageName()).isEqualTo(TEST_FILE_NAME);
    }

    private MockMultipartFile createTestMultipartImageFile() throws Exception {
        FileInputStream fis = new FileInputStream("./src/test/resources/" + TEST_FILE_NAME);
        return new MockMultipartFile("multipartFile", TEST_FILE_NAME, "multipart/form-data", fis);
    }


    @Test
    public void shouldGetSingleProductById() throws Exception {
        // given
        Product newProduct = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        newProduct.setCategory(category);
        productRepository.save(newProduct);
        // when
        MvcResult mvcResult = mockMvc.perform(get("/products/" + newProduct.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andReturn();
        // then
        Product product = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(newProduct.getId());
    }

    @Test
    public void shouldGetProductsByName() throws Exception {
        // given
        Product newProduct = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        newProduct.setCategory(category);
        productRepository.save(newProduct);
        // when
        MvcResult mvcResult = mockMvc.perform(get("/products/search/" + TEST_PRODUCT_NAME))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        List<Product> products = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductPaginationDto.class).getProducts();
        for (Product product: products) {
            assertThat(product.getName()).contains(TEST_PRODUCT_NAME);
        }
    }

    @Test
    public void shouldAddCategory() throws Exception {
        // given
        Category newCategory = createTestCategory();
        // when
        MvcResult mvcResult = mockMvc.perform(post("/products/category")
                        .content(objectMapper.writeValueAsString(newCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        Category category = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Category.class);
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(TEST_CATEGORY_NAME);
    }

    @Test
    public void shouldThrowCategoryAlreadyExistsException() throws Exception {
        // given
        Category newCategory = createTestCategory();
        categoryRepository.save(newCategory);
        Category newCategoryDuplicate = createTestCategory();
        // when
        MvcResult result = mockMvc.perform(post("/products/category")
                        .content(objectMapper.writeValueAsString(newCategoryDuplicate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(result.getResolvedException()).isInstanceOf(CategoryAlreadyExistsException.class);
        assertThat(result.getResolvedException())
                .hasMessageContaining(String.format("Kategoria '%s' już istnieje", newCategoryDuplicate.getName()));
    }

    @Test
    public void shouldGetProductsByCategory() throws Exception {
        //given
        Product testProductOne = createTestProduct();
        Product testProductTwo = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        testProductOne.setCategory(category);
        testProductTwo.setCategory(category);
        productRepository.saveAll(List.of(testProductOne, testProductTwo));
        // when
        MvcResult mvcResult = mockMvc.perform(get("/products/category/" + TEST_CATEGORY_NAME))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        List<Product> products = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ProductPaginationDto.class)
                .getProducts();
        assertThat(products).hasSize(2);
        for (Product p: products) {
            assertThat(p.getCategory().getName()).isEqualTo(TEST_CATEGORY_NAME);
        }
    }

    @Test
    public void shouldDeleteProduct() throws Exception {
        // given
        Product productToDelete = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        productToDelete.setCategory(category);
        productRepository.save(productToDelete);
        // when
        MvcResult result = mockMvc.perform(delete("/products/" + productToDelete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void shouldEditProduct() throws Exception {
        // given
        Product product = createTestProduct();
        Category category = createTestCategory();
        categoryRepository.save(category);
        product.setCategory(category);
        productRepository.save(product);
        // when
        Category newCategory = createTestCategory();
        newCategory.setName("newCategory");
        categoryRepository.save(newCategory);

        Product editedProduct = new Product();
        editedProduct.setId(product.getId());
        editedProduct.setName("testEditedProduct");
        editedProduct.setDescription("testDescription");
        editedProduct.setMainImageName(TEST_FILE_NAME);
        editedProduct.setPrice(BigDecimal.valueOf(29.99));
        editedProduct.setCategory(newCategory);

        MvcResult mvcResult = mockMvc.perform(put("/products/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editedProduct)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        Product editedProductResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(editedProductResult.getName()).isEqualTo("testEditedProduct");
        assertThat(editedProductResult.getDescription()).isEqualTo("testDescription");
        assertThat(editedProductResult.getPrice()).isEqualTo(BigDecimal.valueOf(29.99));
        assertThat(editedProductResult.getCategory().getName()).isEqualTo("newCategory");
    }

    @Test
    public void shouldEditCategory() throws Exception {
        // given
        Category categoryToEdit = createTestCategory();
        categoryRepository.save(categoryToEdit);
        // when
        Category editedCategory = new Category();
        editedCategory.setCategoryId(categoryToEdit.getCategoryId());
        editedCategory.setName("testEditedCategory");

        MvcResult mvcResult = mockMvc.perform(put("/products/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editedCategory)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        Category category = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Category.class);
        assertThat(category.getName()).isEqualTo("testEditedCategory");
    }

    @Test
    public void shouldDeleteCategory() throws Exception {
        // given
        Category categoryToDelete = createTestCategory();
        categoryRepository.save(categoryToDelete);
        // when
        MvcResult result = mockMvc.perform(delete("/products/category/" + categoryToDelete.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // then
        Category deletedCategory = categoryRepository.findByName(TEST_CATEGORY_NAME);
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(deletedCategory).isNull();
    }

    @Test
    public void shouldThrowCategoryInUseException() throws Exception {
        // given
        Product product = createTestProduct();
        Category categoryInUse = createTestCategory();
        categoryRepository.save(categoryInUse);
        product.setCategory(categoryInUse);
        productRepository.save(product);
        // when
        MvcResult result = mockMvc.perform(delete("/products/category/" + categoryInUse.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(result.getResolvedException()).isInstanceOf(CategoryInUseException.class);
    }

}