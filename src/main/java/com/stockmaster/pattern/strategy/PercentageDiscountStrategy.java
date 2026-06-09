package com.stockmaster.pattern.strategy;

/**
 * Strategy (R9): subtracts a percentage of the total (e.g. 10% off).
 */
public class PercentageDiscountStrategy implements DiscountStrategy {

    private final double percentage;

    public PercentageDiscountStrategy(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, was: " + percentage);
        }
        this.percentage = percentage;
    }

    @Override
    public double applyDiscount(double originalAmount) {
        return originalAmount * (1 - percentage / 100.0);
    }
}
