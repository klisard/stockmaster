package com.stockmaster.enums;

/**
 * Lifecycle states of an {@link com.stockmaster.entity.Order}.
 * Persisted as a String via {@code @Enumerated(EnumType.STRING)} so the database
 * stores readable values ("PENDING") rather than fragile ordinal integers.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
