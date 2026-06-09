package com.stockmaster.exception;

/**
 * Thrown when a product cannot be located by id or SKU.
 *
 * <p>Unchecked (extends {@link RuntimeException}) because a missing product
 * during lookup represents a client error (bad id) rather than a recoverable
 * business condition. Mapped to HTTP 404 by the global handler (R10).
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
