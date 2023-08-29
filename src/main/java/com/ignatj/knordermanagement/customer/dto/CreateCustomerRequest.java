package com.ignatj.knordermanagement.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateCustomerRequest {

        @NotBlank(message = "Name is mandatory")
        private String fullName;

        @NotBlank
        @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Invalid email format")
        private String email;

        @NotBlank
        @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$",
                message = "Invalid phone number format")
        private String phoneNumber;
}
