package com.ignatj.knordermanagement.order.service;

import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.order.dto.ChangeProductQuantityRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderResponse;
import com.ignatj.knordermanagement.order.dto.OrderResponse;
import com.ignatj.knordermanagement.order.model.Order;
import com.ignatj.knordermanagement.order.model.OrderLine;
import com.ignatj.knordermanagement.order.repository.OrderLineRepository;
import com.ignatj.knordermanagement.order.repository.OrderRepository;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderLineRepository orderLineRepository, CustomerRepository customerRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponse> getOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public CreateOrderResponse addOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).get(); // Todo: Throw custom Exception

        Order order = new Order();
        customer.addOrder(order);

        for (CreateOrderRequest.OrderLineRequest orderLineRequest : request.getOrderLines()) {
            Product product = productRepository.findById(orderLineRequest.getProductId()).get(); // Todo: Throw custom Exception

            OrderLine orderLine = new OrderLine();
            orderLine.setQuantity(orderLineRequest.getQuantity());

            product.addOrderLine(orderLine);
            order.addOrderLine(orderLine);
        }
        orderRepository.save(order);
        return orderMapper.toCreateOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByDate(String date) {
        LocalDate parsedDate = LocalDate.parse(date); // Todo: Catch and throw custom Exception
        LocalDateTime startOfDay = parsedDate.atStartOfDay();
        LocalDateTime endOfDay = parsedDate.atTime(LocalTime.MAX);

        return orderRepository.findBySubmissionTimeBetween(startOfDay, endOfDay).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public void changeProductQuantityInOrderLine(Long orderId, ChangeProductQuantityRequest request) {
        OrderLine orderLine = orderLineRepository.findByOrder_IdAndProduct_Id(orderId, request.getProductId()).get(); // Todo: Throw custom Exception

        if (request.getNewQuantity() == 0) {
            orderRepository.delete(orderLine.getOrder());
            return;
        }
        orderLine.setQuantity(request.getNewQuantity());
        orderLineRepository.save(orderLine);
    }
}
