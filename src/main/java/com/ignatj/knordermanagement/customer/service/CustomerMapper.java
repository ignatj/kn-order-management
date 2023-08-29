package com.ignatj.knordermanagement.customer.service;

import com.ignatj.knordermanagement.customer.dto.CreateCustomerRequest;
import com.ignatj.knordermanagement.customer.dto.CreateCustomerResponse;
import com.ignatj.knordermanagement.customer.dto.CustomerResponse;
import com.ignatj.knordermanagement.customer.model.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final ModelMapper modelMapper;

    public CustomerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Customer toEntity(CreateCustomerRequest request) {
        return modelMapper.map(request, Customer.class);
    }

    public CreateCustomerResponse toCreateCustomerResponse(Customer customer) {
        return modelMapper.map(customer, CreateCustomerResponse.class);
    }

    public CustomerResponse toCustomerResponse(Customer customer) {
        return modelMapper.map(customer, CustomerResponse.class);
    }
}
