package com.ignatj.knordermanagement.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Order has to contain customer")
    private Long customerId;

    @NotEmpty(message = "Order has to contain products")
    @Valid
    private List<OrderLineRequest> orderLines;

    @Data
    @NoArgsConstructor
    public static class OrderLineRequest {

        @NotNull(message = "Order has to contain product")
        private Long productId;

        @Min(value = 1, message = "Quantity must be 1 or more")
        private int quantity;
    }
}
