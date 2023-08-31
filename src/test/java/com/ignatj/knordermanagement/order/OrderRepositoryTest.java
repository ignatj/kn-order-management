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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KnOrderManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderRepositoryTest extends ContainersEnvironment {

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
    void findsRightBySubmissionTimeBetween() {
        // given
        Order order = new Order();
        Customer customer = Customer.builder()
                .fullName("Example")
                .email("example@example.ee")
                .phoneNumber("+372 5555555")
                .build();
        customer.addOrder(order);
        customerRepository.save(customer); // cascades to order persist

        // when
        LocalDate trueDate = LocalDate.now();
        List<Order> orderList1 = orderRepository.findBySubmissionTimeBetween(trueDate.atStartOfDay(),
                                                                             trueDate.atTime(LocalTime.MAX));
        LocalDate wrongDate = LocalDate.now().minusDays(10);
        List<Order> orderList2 = orderRepository.findBySubmissionTimeBetween(wrongDate.atStartOfDay(),
                                                                             wrongDate.atTime(LocalTime.MAX));

        // then
        assertThat(orderList1).containsExactly(order);
        assertThat(orderList2).isEmpty();
    }

    @Test
    void findsByCustomerId() {
        // given
        Order order = new Order();
        Customer customer = Customer.builder()
                .fullName("Example")
                .email("example@example.ee")
                .phoneNumber("+372 5555555")
                .build();
        customer.addOrder(order);
        customerRepository.save(customer); // cascades to order persist
        Long customerId = customer.getId();

        // when
        List<Order> orderList = orderRepository.findByCustomerIdJPQL(customerId);

        // then
        assertThat(orderList).containsExactly(order);
    }

    @Test
    void findsByProductId() {
        // given
        Order order = new Order();
        OrderLine orderLine = new OrderLine();
        Customer customer = Customer.builder()
                .fullName("Example")
                .email("example@example.ee")
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
        Long productId = product.getId();

        // when
        List<Order> orderList = orderRepository.findByProductIdJPQL(productId);

        // then
        assertThat(orderList).containsExactly(order);
    }
}
