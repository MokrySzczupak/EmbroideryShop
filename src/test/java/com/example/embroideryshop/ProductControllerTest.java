package com.example.embroideryshop;

import com.example.embroideryshop.exception.CategoryAlreadyExistsException;
import com.example.embroideryshop.exception.CategoryInUseException;
import com.example.embroideryshop.model.Category;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;

import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/***
 * Test database records are inserted by liquibase
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private final String defaultMainFileName = "java-logo.png";

    @Test
    @Transactional
    public void shouldAddProduct() throws Exception {
        // given
        FileInputStream fis = new FileInputStream("./src/test/resources/" + defaultMainFileName);
        MockMultipartFile file = new MockMultipartFile("file", defaultMainFileName, "multipart/form-data", fis);
        Product newProduct = new Product();
        newProduct.setName("Poduszka spersonalizowana");
        newProduct.setDescription("Produkt zawiera puch");
        newProduct.setPrice(55.0);
        newProduct.setCategory(categoryRepository.findById(1L).get());
        newProduct.setMainImageName(file.getOriginalFilename());
        System.out.println(newProduct);
        // when
        MvcResult mvcResult = mockMvc.perform(multipart("/products").file(file)
                        .content(objectMapper.writeValueAsString(newProduct)).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andReturn();
        // then
        Product product = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("Poduszka spersonalizowana");
        assertThat(product.getDescription()).isEqualTo("Produkt zawiera puch");
        assertThat(product.getPrice()).isEqualTo(55.0);
        assertThat(product.getMainImageName()).isEqualTo(defaultMainFileName);
    }

    @Test
    @Transactional
    public void shouldGetSingleProductById() throws Exception {
        // given
        Product newProduct = new Product();
        newProduct.setName("Poduszka spersonalizowana");
        newProduct.setPrice(55.0);
        newProduct.setCategory(categoryRepository.findById(1L).get());
        newProduct.setMainImageName(defaultMainFileName);
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
    @Transactional
    public void shouldGetProductsByName() throws Exception {
        // given
        String testName = "spersonalizowana";
        Product newProduct = new Product();
        newProduct.setName("Poduszka " + testName);
        newProduct.setPrice(55.0);
        newProduct.setCategory(categoryRepository.findById(1L).get());
        newProduct.setMainImageName(defaultMainFileName);
        productRepository.save(newProduct);
        // when
        MvcResult mvcResult = mockMvc.perform(get("/products/search/" + testName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        List<Product> products = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Product>>() {});
        for (Product product: products) {
            assertThat(product.getName()).contains("spersonalizowana");
        }
    }

    @Test
    @Transactional
    public void shouldAddCategory() throws Exception {
        Category newCategory = new Category();
        newCategory.setName("testCategory");

        MvcResult mvcResult = mockMvc.perform(post("/products/category")
                        .content(objectMapper.writeValueAsString(newCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Category category = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Category.class);
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(newCategory.getName());
    }

    @Test
    @Transactional
    public void shouldThrowCategoryAlreadyExistsException() throws Exception {
        String categoryName = "testCategory";
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        Category newCategoryDuplicate = new Category();
        newCategoryDuplicate.setName(categoryName);
        categoryRepository.save(newCategory);

        mockMvc.perform(post("/products/category")
                        .content(objectMapper.writeValueAsString(newCategoryDuplicate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryAlreadyExistsException))
                .andExpect(result -> assertEquals("Kategoria '" + newCategoryDuplicate.getName() + "' już istnieje",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @Transactional
    public void shouldGetProductsByCategory() throws Exception {
        String categoryName = "Category 1";

        MvcResult mvcResult = mockMvc.perform(get("/products/category/" + categoryName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        List<Product> products = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Product>>() {});
        for (Product product: products) {
            assertThat(product.getCategory().getName()).isEqualTo(categoryName);
        }
    }

    @Test
    @Transactional
    public void shouldDeleteProduct() throws Exception {
        long productId = 15;
        mockMvc.perform(delete("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void shouldEditProduct() throws Exception {
        Product editedProduct = new Product();
        editedProduct.setId(12L);
        editedProduct.setName("testEditedProduct");
        editedProduct.setDescription("testDescription");
        editedProduct.setPrice(29.99);
        editedProduct.setCategory(categoryRepository.findByName("Category 1"));

        MvcResult mvcResult = mockMvc.perform(put("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editedProduct)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Product product = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(product.getName()).isEqualTo("testEditedProduct");
        assertThat(product.getDescription()).isEqualTo("testDescription");
        assertThat(product.getPrice()).isEqualTo(29.99);
        assertThat(product.getCategory().getName()).isEqualTo("Category 1");
    }

    @Test
    @Transactional
    public void shouldEditCategory() throws Exception {
        Category editedCategory = new Category();
        editedCategory.setCategoryId(1);
        editedCategory.setName("testEditedCategory");

        MvcResult mvcResult = mockMvc.perform(put("/products/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editedCategory)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Category category = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Category.class);
        assertThat(category.getName()).isEqualTo("testEditedCategory");
    }

    @Test
    @Transactional
    public void shouldDeleteCategory() throws Exception {
        long categoryId = 7;
        mockMvc.perform(delete("/products/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowCategoryInUseException() throws Exception {
        long categoryId = 1;
        mockMvc.perform(delete("/products/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryInUseException));
    }

}