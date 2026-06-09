package com.stockmaster.pattern.builder;

import com.stockmaster.entity.Order;
import com.stockmaster.entity.OrderItem;
import com.stockmaster.entity.Product;
import com.stockmaster.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder pattern (R9) for assembling an {@link Order} with its line items.
 *
 * <p>An order has several fields plus a variable number of items; a telescoping
 * constructor would be unwieldy and error-prone. The fluent builder lets callers
 * add items one at a time and validates invariants in {@link #build()}, so an
 * {@code Order} can never be created in a partial/invalid state.
 */
public class OrderBuilder {

    private String customerName;
    private OrderStatus status;
    private double totalAmount;
    private final List<OrderItem> items = new ArrayList<>();

    public OrderBuilder customerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public OrderBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder totalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public OrderBuilder addItem(Product product, int quantity, double unitPrice) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        items.add(item);
        return this;
    }

    public Order build() {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalStateException("Order must have a customer name.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must contain at least one item.");
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setStatus(status != null ? status : OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // Wire the bidirectional link so cascade persistence works correctly.
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);
        return order;
    }
}
