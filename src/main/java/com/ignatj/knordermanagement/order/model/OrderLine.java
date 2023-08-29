package com.ignatj.knordermanagement.order.model;

import com.ignatj.knordermanagement.product.model.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "order_lines", uniqueConstraints = {
        @UniqueConstraint(name = "Order contains duplicate products", columnNames = {"order_id", "product_id"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Product product;

    @Override
    public int hashCode() {
        return 20;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderLine other = (OrderLine) obj;
        return id != null && id.equals(other.getId());
    }
}
