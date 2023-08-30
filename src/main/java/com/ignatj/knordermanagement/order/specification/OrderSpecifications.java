package com.ignatj.knordermanagement.order.specification;

import com.ignatj.knordermanagement.order.model.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {

    public static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Order> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("orderLines").get("product").get("id"), productId);
    }
}
