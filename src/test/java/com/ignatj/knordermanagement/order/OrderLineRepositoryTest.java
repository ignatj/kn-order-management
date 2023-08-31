package com.ignatj.knordermanagement.order;

import com.ignatj.knordermanagement.KnOrderManagementApplication;
import com.ignatj.knordermanagement.config.ContainersEnvironment;
import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.order.model.Order;
import com.ignatj.knordermanagement.order.model.OrderLine;
import com.ignatj.knordermanagement.order.repository.OrderLineRepository;
import com.ignatj.knordermanagement.order.repository.OrderRepository;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KnOrderManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderLineRepositoryTest extends ContainersEnvironment {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderLineRepository orderLineRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        orderLineRepository.deleteAll();
    }

    @Test
    void findsOrderLineByOrderIdAndProductId() {
        // given
        Order order = new Order();
        OrderLine orderLine = new OrderLine();
        Customer customer = Customer.builder()
                .fullName("Example")
                .email("example1@example.ee")
                .phoneNumber("+372 5555555")
                .build();
        Product product = Product.builder()
                .name("Example")
                .skuCode("adasd")
                .unitPrice(BigDecimal.ONE)
                .build();
        productRepository.save(product);
        customer.addOrder(order);
        order.addOrderLine(orderLine);
        product.addOrderLine(orderLine);
        customerRepository.save(customer); // cascades to order and OrderLine persist
        Long wrongOrderId = 15L;
        Long wrongProductId = 65L;

        // when
        Optional<OrderLine> orderLineOptional1 = orderLineRepository.findByOrder_IdAndProduct_Id(order.getId(),
                                                                                                product.getId());
        Optional<OrderLine> orderLineOptional2 = orderLineRepository.findByOrder_IdAndProduct_Id(wrongOrderId,
                                                                                                 wrongProductId);

        // then
        assertThat(orderLineOptional1).contains(orderLine);
        assertThat(orderLineOptional2).isEmpty();
    }
}
