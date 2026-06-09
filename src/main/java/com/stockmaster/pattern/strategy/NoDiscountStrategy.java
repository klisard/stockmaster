package com.stockmaster.pattern.strategy;

/**
 * Strategy (R9): no discount - returns the amount unchanged.
 * Acts as a null-object default so callers never need a null check.
 */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double originalAmount) {
        return originalAmount;
    }
}
