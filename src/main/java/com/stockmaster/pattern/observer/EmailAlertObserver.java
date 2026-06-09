package com.stockmaster.pattern.observer;

import com.stockmaster.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Concrete observer (R9) that simulates sending a low-stock email alert.
 * In a production system this would integrate with a mail service; here it
 * prints to demonstrate that multiple observers react independently.
 */
@Component
public class EmailAlertObserver implements StockObserver {

    @Override
    public void onLowStock(Product product) {
        System.out.printf("[EMAIL] To purchasing: reorder '%s' (SKU: %s) - %d remaining.%n",
                product.getName(), product.getSku(), product.getStockQuantity());
    }
}
