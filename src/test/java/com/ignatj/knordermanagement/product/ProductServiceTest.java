package com.ignatj.knordermanagement.product;

import com.ignatj.knordermanagement.product.dto.CreateProductRequest;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import com.ignatj.knordermanagement.product.service.ProductMapper;
import com.ignatj.knordermanagement.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productMockRepository;
    private ProductService productService;
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mockito.mock(ProductMapper.class);
        productService = new ProductService(productMockRepository, productMapper);
    }

    @Test
    void getsAllProducts() {
        // when
        productService.getProducts();

        // then
        verify(productMockRepository).findAll();
    }

    @Test
    void addsProduct() {
        // given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cola");
        request.setUnitPrice(BigDecimal.ONE);
        request.setSkuCode("sfdsf");

        Product product = productMapper.toEntity(request);

        // when
        productService.addProduct(request);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productMockRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();
        assertThat(product).isEqualTo(capturedProduct);
    }
}
