package com.ignatj.knordermanagement.customer.repository;

import com.ignatj.knordermanagement.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}