package com.stockmaster.pattern.strategy;

/**
 * Strategy interface (R9). Encapsulates an interchangeable discount algorithm.
 *
 * <p>This is also a functional interface, so it could be supplied as a lambda,
 * reinforcing the link between the Strategy pattern and functional programming
 * (R3) taught in Week 4 / Week 13.
 */
@FunctionalInterface
public interface DiscountStrategy {

    /**
     * @param originalAmount the pre-discount order total
     * @return the amount payable after applying this discount
     */
    double applyDiscount(double originalAmount);
}
