package com.ignatj.knordermanagement.product;

import com.ignatj.knordermanagement.KnOrderManagementApplication;
import com.ignatj.knordermanagement.config.ContainersEnvironment;
import com.ignatj.knordermanagement.product.dto.CreateProductRequest;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import com.ignatj.knordermanagement.product.service.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = KnOrderManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIntegrationTest extends ContainersEnvironment {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void createsProductAndReturnsCreateProductResponse() throws Exception {
        // given
        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setSkuCode("sdfsd");
        productRequest.setUnitPrice(BigDecimal.ONE);
        productRequest.setName("Fanta");

        // when & then
        mockMvc.perform(post("/api/products")
                        .content(new ObjectMapper().writeValueAsString(productRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists());
    }

    @Test
    void returnsErrorResponseAfterInvalidProductRequestArguments() throws Exception {
        // given
        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setSkuCode("");
        productRequest.setUnitPrice(BigDecimal.valueOf(-1L));
        productRequest.setName("");


        // when & then
        mockMvc.perform(post("/api/products")
                        .content(new ObjectMapper().writeValueAsString(productRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to validate the input"))
                .andExpect(jsonPath("$.errors.skuCode").value("must not be blank"))
                .andExpect(jsonPath("$.errors.name").value("Name is mandatory"))
                .andExpect(jsonPath("$.errors.unitPrice").value("Price should be positive"));
    }

    @Test
    void returnsErrorResponseAfterProductCreationWithNonUniqueSkuCode() throws Exception {
        // given
        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setSkuCode("sdfsd");
        productRequest.setUnitPrice(BigDecimal.ONE);
        productRequest.setName("Fanta");

        productRepository.save(productMapper.toEntity(productRequest));

        // when & then
        mockMvc.perform(post("/api/products")
                        .content(new ObjectMapper().writeValueAsString(productRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unique skuCode constraint"));
    }
}
