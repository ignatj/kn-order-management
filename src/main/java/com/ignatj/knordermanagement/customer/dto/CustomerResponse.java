package com.ignatj.knordermanagement.customer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CustomerResponse {

    private Long customerId;

    private String fullName;

    private String email;

    private String phoneNumber;
}
