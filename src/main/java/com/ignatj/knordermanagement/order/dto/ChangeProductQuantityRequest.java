package com.ignatj.knordermanagement.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull
    private Long productId;

    @Min(value = 0, message = "Quantity can not be negative")
    private int newQuantity;
}
