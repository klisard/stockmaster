package com.stockmaster.exception;

/**
 * Thrown when an order requests more units than are in stock.
 *
 * <p>Deliberately a <b>checked</b> exception (extends {@link Exception}, not
 * {@code RuntimeException}): insufficient stock is an anticipated, recoverable
 * business condition, so the compiler forces callers to acknowledge and handle
 * it. This is the correct use of checked exceptions taught in Week 2 (R10).
 * Mapped to HTTP 409 Conflict by the global handler.
 */
public class InsufficientStockException extends Exception {

    private final int available;
    private final int requested;

    public InsufficientStockException(String message) {
        super(message);
        this.available = -1;
        this.requested = -1;
    }

    public InsufficientStockException(String message, int available, int requested) {
        super(message);
        this.available = available;
        this.requested = requested;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}
