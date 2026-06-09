package com.stockmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stockmaster.enums.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A customer order. Owns a one-to-many relationship with {@link OrderItem}
 * (cascade ALL + orphan removal, so items live and die with their order).
 *
 * <p>The {@code @Version} field enables optimistic locking (R5): if two
 * transactions modify the same order concurrently, the second commit fails with
 * an {@code OptimisticLockingFailureException} instead of silently overwriting.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    /** Optimistic-lock version counter, managed automatically by Hibernate. */
    @Version
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"order", "hibernateLazyInitializer"})
    private List<OrderItem> items = new ArrayList<>();

    /** No-arg constructor required by the JPA specification. */
    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
