package com.stockmaster.exception;

/**
 * Thrown when creating a product whose SKU already exists. Unchecked;
 * mapped to HTTP 409 Conflict by the global handler (R10).
 */
public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String message) {
        super(message);
    }
}
