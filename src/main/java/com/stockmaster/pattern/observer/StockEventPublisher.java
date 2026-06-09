package com.stockmaster.pattern.observer;

import com.stockmaster.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject in the Observer pattern (R9).
 *
 * <p>Holds the list of registered {@link StockObserver}s (R1: ArrayList - order
 * of notification is preserved). Spring injects all {@code StockObserver} beans
 * via the constructor, so adding a new observer requires zero changes here.
 */
@Component
public class StockEventPublisher {

    private final List<StockObserver> observers = new ArrayList<>();

    /**
     * Spring autowires every {@link StockObserver} bean into this list.
     * @param observers all observer implementations discovered by component scan
     */
    public StockEventPublisher(List<StockObserver> observers) {
        this.observers.addAll(observers);
    }

    public void subscribe(StockObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(StockObserver observer) {
        observers.remove(observer);
    }

    /** Notifies every registered observer that a product is low on stock. */
    public void notifyLowStock(Product product) {
        for (StockObserver observer : observers) {
            observer.onLowStock(product);
        }
    }
}
