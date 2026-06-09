package com.stockmaster.pattern.strategy;

/**
 * Strategy (R9): subtracts a fixed amount, never dropping below zero.
 */
public class FlatDiscountStrategy implements DiscountStrategy {

    private final double flatAmount;

    public FlatDiscountStrategy(double flatAmount) {
        if (flatAmount < 0) {
            throw new IllegalArgumentException("Flat discount must not be negative, was: " + flatAmount);
        }
        this.flatAmount = flatAmount;
    }

    @Override
    public double applyDiscount(double originalAmount) {
        return Math.max(0.0, originalAmount - flatAmount);
    }
}
