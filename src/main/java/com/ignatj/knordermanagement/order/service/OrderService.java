package com.ignatj.knordermanagement.order.service;

import com.ignatj.knordermanagement.common.exception.ApiException;
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
import com.ignatj.knordermanagement.order.specification.OrderSpecifications;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ApiException("Customer with id {" + request.getCustomerId() + "} does not exist"));

        Order order = new Order();
        customer.addOrder(order);

        for (CreateOrderRequest.OrderLineRequest orderLineRequest : request.getOrderLines()) {
            Product product = productRepository.findById(orderLineRequest.getProductId())
                    .orElseThrow(() ->
                            new ApiException("Product with id {" + orderLineRequest.getProductId() + "} does not exist"));

            OrderLine orderLine = new OrderLine();
            orderLine.setQuantity(orderLineRequest.getQuantity());

            product.addOrderLine(orderLine);
            order.addOrderLine(orderLine);
        }
        orderRepository.save(order);
        return orderMapper.toCreateOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            LocalDateTime startOfDay = parsedDate.atStartOfDay();
            LocalDateTime endOfDay = parsedDate.atTime(LocalTime.MAX);

            return orderRepository.findBySubmissionTimeBetween(startOfDay, endOfDay).stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
        } catch (DateTimeParseException ex) {
            throw new ApiException("Invalid date format: " + date);
        }
    }

    public void changeProductQuantityInOrderLine(Long orderId, ChangeProductQuantityRequest request) {
        OrderLine orderLine = orderLineRepository.findByOrder_IdAndProduct_Id(orderId, request.getProductId())
                .orElseThrow(() -> new ApiException("No order line with those attributes exist: orderId{"
                        + orderId + "} , product{" + request.getProductId() + "}"));
        if (request.getNewQuantity() == 0) {
            orderRepository.delete(orderLine.getOrder());
            return;
        }
        orderLine.setQuantity(request.getNewQuantity());
        orderLineRepository.save(orderLine);
    }

    public List<OrderResponse> getOrdersByCustomerJPQL(Long customerId) {
        return orderRepository.findByCustomerIdJPQL(customerId).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByProductJPQL(Long productId) {
        return orderRepository.findByProductIdJPQL(productId).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByCustomerSpecification(Long customerId) {
        Specification<Order> orderSpecification = OrderSpecifications.hasCustomerId(customerId);
        return orderRepository.findAll(orderSpecification).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByProductSpecification(Long productId) {
        Specification<Order> orderSpecification = OrderSpecifications.hasProductId(productId);
        return orderRepository.findAll(orderSpecification).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
