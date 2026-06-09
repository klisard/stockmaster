package com.stockmaster.exception;

/**
 * Thrown when an order cannot be located by id. Unchecked; mapped to HTTP 404 (R10).
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
