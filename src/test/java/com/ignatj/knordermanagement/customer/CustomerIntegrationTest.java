package com.ignatj.knordermanagement.customer;

import com.ignatj.knordermanagement.KnOrderManagementApplication;
import com.ignatj.knordermanagement.config.ContainersEnvironment;
import com.ignatj.knordermanagement.customer.dto.CreateCustomerRequest;
import com.ignatj.knordermanagement.customer.repository.CustomerRepository;
import com.ignatj.knordermanagement.customer.service.CustomerMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = KnOrderManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest extends ContainersEnvironment {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void createsCustomerAndReturnsCreateCustomerResponse() throws Exception {
        // given
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setFullName("Example Name");
        customerRequest.setEmail("example@example.ee");
        customerRequest.setPhoneNumber("+372 53562052");

        // when & then
        mockMvc.perform(post("/api/customers")
                        .content(new ObjectMapper().writeValueAsString(customerRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").exists());
    }

    @Test
    void returnsErrorResponseAfterInvalidCustomerRequestArguments() throws Exception {
        // given
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setFullName("");
        customerRequest.setEmail("wrong@email");
        customerRequest.setPhoneNumber("911");

        // when & then
        mockMvc.perform(post("/api/customers")
                        .content(new ObjectMapper().writeValueAsString(customerRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to validate the input"))
                .andExpect(jsonPath("$.errors.phoneNumber").value("Invalid phone number format"))
                .andExpect(jsonPath("$.errors.fullName").value("Name is mandatory"))
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }

    @Test
    void returnsErrorResponseAfterCustomerCreationWithNonUniqueEmail() throws Exception {
        // given
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setFullName("Example Name");
        customerRequest.setEmail("example@example.ee");
        customerRequest.setPhoneNumber("+372 53562052");

        customerRepository.save(customerMapper.toEntity(customerRequest));

        // when & then
        mockMvc.perform(post("/api/customers")
                        .content(new ObjectMapper().writeValueAsString(customerRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unique email constraint"));
    }
}
