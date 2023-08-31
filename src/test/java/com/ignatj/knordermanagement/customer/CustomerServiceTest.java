package com.ignatj.knordermanagement.customer;

import com.ignatj.knordermanagement.customer.dto.CreateCustomerRequest;
import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.customer.service.CustomerMapper;
import com.ignatj.knordermanagement.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerMockRepository;
    private CustomerService customerService;
    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = Mockito.mock(CustomerMapper.class);
        customerService = new CustomerService(customerMockRepository, customerMapper);
    }

    @Test
    public void getsAllCustomers() {
        // when
        customerService.getCustomers();

        // then
        verify(customerMockRepository).findAll();
    }

    @Test
    public void addsCustomer() {
        // given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFullName("Example Name");
        request.setEmail("example@email.qq");
        request.setPhoneNumber("+372 53562052");

        Customer customer = customerMapper.toEntity(request);

        // when
        customerService.addCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerMockRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(customer).isEqualTo(capturedCustomer);
    }
}
