package com.example.embroideryshop;

import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.repository.CategoryRepository;
import com.example.embroideryshop.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    public void shouldAddProduct() throws Exception {
        // given
        Product newProduct = new Product();
        newProduct.setName("Poduszka spersonalizowana");
        newProduct.setDescription("Produkt zawiera puch");
        newProduct.setPrice(55.0);
        newProduct.setCategory(categoryRepository.findById(1L).get());
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
        assertThat(product.getName()).isEqualTo("Poduszka spersonalizowana");
        assertThat(product.getDescription()).isEqualTo("Produkt zawiera puch");
        assertThat(product.getPrice()).isEqualTo(55.0);
    }

    @Test
    @Transactional
    public void shouldGetSingleProductById() throws Exception {
        // given
        Product newProduct = new Product();
        newProduct.setName("Poduszka spersonalizowana");
        newProduct.setPrice(55.0);
        newProduct.setCategory(categoryRepository.findById(1L).get());
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

}
