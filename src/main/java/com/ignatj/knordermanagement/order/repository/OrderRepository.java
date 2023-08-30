package com.ignatj.knordermanagement.order.repository;

import com.ignatj.knordermanagement.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findBySubmissionTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerIdJPQL(Long customerId);

    @Query("SELECT o FROM Order o JOIN o.orderLines ol WHERE ol.product.id = :productId")
    List<Order> findByProductIdJPQL(Long productId);
}
