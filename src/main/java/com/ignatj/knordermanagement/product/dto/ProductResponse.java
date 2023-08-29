package com.ignatj.knordermanagement.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class ProductResponse {

    private Long productId;

    private String skuCode;

    private String name;

    private BigDecimal unitPrice;
}
