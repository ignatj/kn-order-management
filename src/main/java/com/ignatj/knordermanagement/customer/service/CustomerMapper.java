package com.ignatj.knordermanagement.customer.service;

import com.ignatj.knordermanagement.customer.dto.CreateCustomerRequest;
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
}
