package com.ignatj.knordermanagement.order;

import com.ignatj.knordermanagement.KnOrderManagementApplication;
import com.ignatj.knordermanagement.config.ContainersEnvironment;
import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.order.dto.ChangeProductQuantityRequest;
import com.ignatj.knordermanagement.order.dto.CreateOrderRequest;
import com.ignatj.knordermanagement.order.model.Order;
import com.ignatj.knordermanagement.order.model.OrderLine;
import com.ignatj.knordermanagement.order.repository.OrderLineRepository;
import com.ignatj.knordermanagement.product.model.Product;
import com.ignatj.knordermanagement.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = KnOrderManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderIntegrationTest extends ContainersEnvironment {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        productRepository.deleteAll();
        orderLineRepository.deleteAll();
    }

    @Test
    void createsOrderAndReturnsAppropriateResponse() throws Exception {
        // given
        Customer customer = Customer.builder()
                        .phoneNumber("+372 53562052")
                        .email("example@example.ee")
                        .fullName("Example Name")
                        .build();
        Product product = Product.builder()
                        .skuCode("qaeqw")
                        .unitPrice(BigDecimal.ONE)
                        .name("Cola")
                        .build();
        customerRepository.save(customer);
        productRepository.save(product);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        orderLineRequest.setProductId(product.getId());
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(customer.getId());

        // when & then
        mockMvc.perform(post("/api/orders")
                .content(new ObjectMapper().writeValueAsString(orderRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    void returnsErrorResponseAfterCreatingOrderWhereCustomerIdDoesNotExist() throws Exception {
        // given
        Customer customer = Customer.builder()
                .phoneNumber("+372 53562052")
                .email("example@example.ee")
                .fullName("Example Name")
                .build();
        Product product = Product.builder()
                .skuCode("qaeqw")
                .unitPrice(BigDecimal.ONE)
                .name("Cola")
                .build();
        customerRepository.save(customer);
        productRepository.save(product);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        orderLineRequest.setProductId(1L);
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(1651L);

        // when & then
        mockMvc.perform(post("/api/orders")
                .content(new ObjectMapper().writeValueAsString(orderRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Customer with id {" + orderRequest.getCustomerId() + "} does not exist"));
    }

    @Test
    void returnsErrorResponseAfterCreatingOrderWhereProductIdDoesNotExist() throws Exception {
        // given
        Customer customer = Customer.builder()
                .phoneNumber("+372 53562052")
                .email("example@example.ee")
                .fullName("Example Name")
                .build();
        Product product = Product.builder()
                .skuCode("qaeqw")
                .unitPrice(BigDecimal.ONE)
                .name("Cola")
                .build();
        customerRepository.save(customer);
        productRepository.save(product);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(1);
        Long wrongProductId = 165165L;
        orderLineRequest.setProductId(wrongProductId);
        orderRequest.setOrderLines(List.of(orderLineRequest));
        orderRequest.setCustomerId(customer.getId());

        // when & then
        mockMvc.perform(post("/api/orders")
                        .content(new ObjectMapper().writeValueAsString(orderRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Product with id {" + wrongProductId + "} does not exist"));
    }

    @Test
    void returnsErrorResponseAfterCreatingOrderWithInvalidArguments() throws Exception {
        // given
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderLineRequest orderLineRequest = new CreateOrderRequest.OrderLineRequest();
        orderLineRequest.setQuantity(-14);
        orderRequest.setOrderLines(List.of(orderLineRequest));

        // when & then
        mockMvc.perform(post("/api/orders")
                    .content(new ObjectMapper().writeValueAsString(orderRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to validate the input"))
                .andExpect(jsonPath("$.errors.customerId").value("Order has to contain customer"))
                .andExpect(jsonPath("$.errors['orderLines[0].quantity']").value("Quantity must be 1 or more"))
                .andExpect(jsonPath("$.errors['orderLines[0].productId']").value("Order has to contain product"));
    }

    @Test
    void getsOrdersByDate() throws Exception {
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
        String date = LocalDate.now().toString();

        // when & then
        mockMvc.perform(get("/api/orders/date/{date}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(order.getId()));
    }

    @Test
    void returnsErrorResponseWhenInvalidDateFormat() throws Exception {
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
        String wrongDate = "01.010.0";

        // when & then
        mockMvc.perform(get("/api/orders/date/{date}", wrongDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid date format: " + wrongDate));
    }

    @Test
    void changesProductQuantityInOrderLine() throws Exception {
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

        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setNewQuantity(55);
        request.setProductId(product.getId());

        // when & then
        mockMvc.perform(patch("/api/orders/{orderId}", order.getId())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        OrderLine changedOrderLine = orderLineRepository.findById(orderLine.getId()).get();

        assertThat(changedOrderLine.getQuantity()).isEqualTo(request.getNewQuantity());
    }

    @Test
    void returnsErrorResponseWhenChangeQuantity() throws Exception {
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

        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setNewQuantity(55);
        request.setProductId(1213L);

        // when & then
        mockMvc.perform(patch("/api/orders/{orderId}", order.getId())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("No order line with those attributes exist: orderId{"
                        + order.getId() + "} , product{" + request.getProductId() + "}"));
    }

    @Test
    void getsOrdersByCustomerJPQL() throws Exception {
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

        String customerId = customer.getId().toString();

        // when & then
         mockMvc.perform(get("/api/orders/search-by-customer-jpql/{customerId}", customerId))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$[0].orderId").value(order.getId()));
    }

    @Test
    void getsOrdersByProductJPQL() throws Exception {
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

        String productId = product.getId().toString();

        // when & then
        mockMvc.perform(get("/api/orders/search-by-product-jpql/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(order.getId()));
    }

    @Test
    void getsOrdersByCustomerSpecification() throws Exception {
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

        String customerId = customer.getId().toString();

        // when & then
        mockMvc.perform(get("/api/orders/search-by-customer-specification/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(order.getId()));
    }

    @Test
    void getsOrdersByProductSpecification() throws Exception {
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

        String productId = product.getId().toString();

        // when & then
        mockMvc.perform(get("/api/orders/search-by-product-specification/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(order.getId()));
    }
}
