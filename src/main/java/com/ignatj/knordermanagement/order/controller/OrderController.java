package com.ignatj.knordermanagement.order.controller;

import com.ignatj.knordermanagement.order.dto.ChangeProductQuantityRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderResponse;
import com.ignatj.knordermanagement.order.dto.OrderResponse;
import com.ignatj.knordermanagement.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.addOrder(request);
    }

    @GetMapping("/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByDate(@PathVariable String date) {
        return orderService.getOrdersByDate(date);
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeProductQuantityInOrderLine(@PathVariable Long orderId,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        orderService.changeProductQuantityInOrderLine(orderId, request);
    }
}
