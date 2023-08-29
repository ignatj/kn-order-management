package com.ignatj.knordermanagement.order.repository;

import com.ignatj.knordermanagement.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
