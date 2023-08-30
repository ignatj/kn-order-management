package com.ignatj.knordermanagement.order.repository;

import com.ignatj.knordermanagement.order.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    Optional<OrderLine> findByOrder_IdAndProduct_Id(Long order_id, Long product_id);
}
