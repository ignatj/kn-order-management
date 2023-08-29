package com.ignatj.knordermanagement.customer.service;

import com.ignatj.knordermanagement.customer.dto.CreateCustomerRequest;
import com.ignatj.knordermanagement.customer.dto.CreateCustomerResponse;
import com.ignatj.knordermanagement.customer.dto.CustomerResponse;
import com.ignatj.knordermanagement.customer.model.Customer;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse)
                .collect(Collectors.toList());
    }

    public CreateCustomerResponse addCustomer(CreateCustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);
        customerRepository.save(customer);
        return customerMapper.toCreateCustomerResponse(customer);
    }
}
