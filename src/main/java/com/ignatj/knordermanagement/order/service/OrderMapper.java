package com.ignatj.knordermanagement.order.service;

import com.ignatj.knordermanagement.order.dto.CreateOrderRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderResponse;
import com.ignatj.knordermanagement.order.dto.OrderResponse;
import com.ignatj.knordermanagement.order.model.Order;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final ModelMapper modelMapper;

    public OrderMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Order toEntity(CreateOrderRequest request) {
        return modelMapper.map(request, Order.class);
    }

    public CreateOrderResponse toCreateOrderResponse (Order order) {
        return modelMapper.map(order, CreateOrderResponse.class);
    }

    public OrderResponse toOrderResponse(Order order) {
        return modelMapper.map(order, OrderResponse.class);
    }
}
