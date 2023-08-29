package com.ignatj.knordermanagement.product.model;

import com.ignatj.knordermanagement.order.model.OrderLine;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "Unique skuCode constraint", columnNames = {"skuCode"})
})
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String skuCode;

    private String name;

    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<OrderLine> orderLines = new HashSet<>();

    @Builder
    public Product(String skuCode, String name, BigDecimal unitPrice) {
        this.skuCode = skuCode;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setProduct(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(skuCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        return Objects.equals(skuCode, other.getSkuCode());
    }
}
