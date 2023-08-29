package com.ignatj.knordermanagement.product.controller;

import com.ignatj.knordermanagement.product.dto.CreateProductRequest;
import com.ignatj.knordermanagement.product.dto.CreateProductResponse;
import com.ignatj.knordermanagement.product.dto.ProductResponse;
import com.ignatj.knordermanagement.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.addProduct(request);
    }
}
