package com.ignatj.knordermanagement.order;

import com.ignatj.knordermanagement.common.exception.ApiException;
import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.order.dto.ChangeProductQuantityRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderRequest;
import com.ignatj.knordermanagement.order.model.Order;
import com.ignatj.knordermanagement.order.model.OrderLine;
import com.ignatj.knordermanagement.order.repository.OrderLineRepository;
import com.ignatj.knordermanagement.order.repository.OrderRepository;
import com.ignatj.knordermanagement.order.service.OrderMapper;
import com.ignatj.knordermanagement.order.service.OrderService;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderMockRepository;
    @Mock
    private OrderLineRepository orderLineMockRepository;
    @Mock
    private CustomerRepository customerMockRepository;
    @Mock
    private ProductRepository productMockRepository;

    private OrderService orderService;
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mockito.mock(OrderMapper.class);
        orderService = new OrderService(orderMockRepository,
                                        orderLineMockRepository,
                                        customerMockRepository,
                                        productMockRepository,
                                        orderMapper);
    }

    @Test
    void addsOrder() {
        // given
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        orderLineRequest.setProductId(1L);
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(1L);

        given(customerMockRepository.findById(anyLong()))
                .willReturn(Optional.of(new Customer()));

        given(productMockRepository.findById(anyLong()))
                .willReturn(Optional.of(new Product()));

        Order order = orderMapper.toEntity(orderRequest);

        // when
        orderService.addOrder(orderRequest);

        // then
        verify(orderMockRepository).save(any(Order.class));
    }

    @Test
    void throwsExceptionWhenRequestContainsWrongCustomerId() {
        // given
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        orderLineRequest.setProductId(1L);
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(1L);

        given(customerMockRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> orderService.addOrder(orderRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Customer with id {" + 1 + "} does not exist");
    }

    @Test
    void throwsExceptionWhenRequestContainsWrongProductId() {
        // given
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        orderLineRequest.setProductId(1L);
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(1L);

        given(customerMockRepository.findById(anyLong()))
                .willReturn(Optional.of(new Customer()));

        given(productMockRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.addOrder(orderRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Product with id {" + 1 + "} does not exist");
    }

    @Test
    void throwsExceptionWhenWrongDateFormat() {
        // given
        String wrongDate = "32.13.2023";

        // when & then
        assertThatThrownBy(() -> orderService.getOrdersByDate(wrongDate))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid date format: " + wrongDate);
    }

    @Test
    void changesProductQuantityInOrderLine() {
        // given
        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setProductId(1L);
        request.setNewQuantity(1);

        given(orderLineMockRepository.findByOrder_IdAndProduct_Id(anyLong(), anyLong()))
                .willReturn(Optional.of(new OrderLine()));

        // when
        orderService.changeProductQuantityInOrderLine(1L, request);

        // then
        verify(orderLineMockRepository).save(any(OrderLine.class));
    }

    @Test
    void throwsExceptionWhenCalledWithWrongOrderLine() {
        // given
        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setProductId(1L);
        request.setNewQuantity(1);
        Long notExistentOrderId = 0L;

        given(orderLineMockRepository.findByOrder_IdAndProduct_Id(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.changeProductQuantityInOrderLine(notExistentOrderId, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("No order line with those attributes exist: orderId{"
                        + notExistentOrderId + "} , product{" + request.getProductId() + "}");
    }

    @Test
    void deletesOrderIfQuantityOfCorrespondingOrderLineChangesToZero() {
        // given
        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setProductId(1L);
        request.setNewQuantity(0);

        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(new Order());

        given(orderLineMockRepository.findByOrder_IdAndProduct_Id(anyLong(), anyLong()))
                .willReturn(Optional.of(orderLine));

        // when
        orderService.changeProductQuantityInOrderLine(1L, request);

        // then
        verify(orderMockRepository).delete(any(Order.class));
    }

    @Test
    void getsOrdersByCustomerSpecification() {
        // given
        Long customerId = 1L;

        // when
        orderService.getOrdersByCustomerSpecification(customerId);

        // then
        verify(orderMockRepository).findAll(any(Specification.class));
    }

    @Test
    void getsOrdersByProductSpecification() {
        // given
        Long productId = 1L;

        // when
        orderService.getOrdersByCustomerSpecification(productId);

        // then
        verify(orderMockRepository).findAll(any(Specification.class));
    }
}
