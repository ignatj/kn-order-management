package com.ignatj.knordermanagement.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateProductRequest {

        @NotBlank
        private String skuCode;

        @NotBlank(message = "Name is mandatory")
        private String name;

        @DecimalMin(value = "0", message = "Price should be positive")
        private BigDecimal unitPrice;
}
