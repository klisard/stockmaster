package com.stockmaster.pattern.observer;

import com.stockmaster.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete observer (R9) that writes a WARN log line when stock runs low.
 */
@Component
public class LoggingAlertObserver implements StockObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingAlertObserver.class);

    @Override
    public void onLowStock(Product product) {
        log.warn("[LOW STOCK] '{}' (SKU: {}) - only {} units left.",
                product.getName(), product.getSku(), product.getStockQuantity());
    }
}
