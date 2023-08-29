package com.ignatj.knordermanagement.product.service;

import com.ignatj.knordermanagement.product.dto.CreateProductRequest;
import com.ignatj.knordermanagement.product.dto.CreateProductResponse;
import com.ignatj.knordermanagement.product.dto.ProductResponse;
import com.ignatj.knordermanagement.product.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product toEntity(CreateProductRequest request) {
        return modelMapper.map(request, Product.class);
    }

    public CreateProductResponse toCreateProductResponse (Product product) {
        return modelMapper.map(product, CreateProductResponse.class);
    }

    public ProductResponse toProductResponse (Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }
}
