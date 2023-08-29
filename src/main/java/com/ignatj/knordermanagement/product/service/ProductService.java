package com.ignatj.knordermanagement.product.service;

import com.ignatj.knordermanagement.product.dto.CreateProductRequest;
import com.ignatj.knordermanagement.product.dto.CreateProductResponse;
import com.ignatj.knordermanagement.product.dto.ProductResponse;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public CreateProductResponse addProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        productRepository.save(product);
        return productMapper.toCreateProductResponse(product);
    }
}
