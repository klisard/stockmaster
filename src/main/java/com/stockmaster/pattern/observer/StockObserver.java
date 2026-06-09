package com.stockmaster.pattern.observer;

import com.stockmaster.entity.Product;

/**
 * Observer interface in the Observer pattern (R9).
 *
 * <p>Implementations are notified whenever a product's stock drops below the
 * low-stock threshold, without the publisher knowing anything about what each
 * observer actually does (logging, email, etc.). This decouples the stock-alert
 * policy from the product-update pathway.
 */
public interface StockObserver {

    void onLowStock(Product product);
}
