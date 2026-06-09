package com.stockmaster.service;

import com.stockmaster.entity.Order;
import com.stockmaster.entity.Product;
import com.stockmaster.enums.OrderStatus;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.exception.OrderNotFoundException;
import com.stockmaster.exception.ProductNotFoundException;
import com.stockmaster.pattern.builder.OrderBuilder;
import com.stockmaster.pattern.strategy.DiscountStrategy;
import com.stockmaster.repository.OrderRepository;
import com.stockmaster.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Business logic for orders. Brings together several requirements:
 * <ul>
 *   <li>R5 - a {@link ReentrantLock} serialises stock deduction so two
 *       concurrent orders can never oversell the same product.</li>
 *   <li>R9 - {@link OrderBuilder} (Builder) and {@link DiscountStrategy} (Strategy).</li>
 *   <li>R10 - throws the checked {@link InsufficientStockException}.</li>
 *   <li>R4 - Stream aggregations for grouping and revenue.</li>
 * </ul>
 */
@Service
public class OrderService {

    /** Fair lock (true) hands the lock to the longest-waiting thread, avoiding starvation. */
    private final ReentrantLock stockLock = new ReentrantLock(true);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * Places an order: validates stock, deducts it, builds the order via the
     * Builder, and applies the chosen discount Strategy.
     *
     * @param customerName     buyer name
     * @param itemMap          productId -> quantity (R1: Map)
     * @param discountStrategy R9: discount algorithm chosen at runtime
     * @throws InsufficientStockException R10: checked - a recoverable business condition
     */
    @Transactional
    public Order placeOrder(String customerName,
                            Map<Long, Integer> itemMap,
                            DiscountStrategy discountStrategy) throws InsufficientStockException {

        // R5: only one thread at a time may read-then-write stock levels.
        stockLock.lock();
        try {
            OrderBuilder builder = new OrderBuilder()
                    .customerName(customerName)
                    .status(OrderStatus.PENDING);

            double rawTotal = 0.0;
            for (Map.Entry<Long, Integer> entry : itemMap.entrySet()) {
                Long productId = entry.getKey();
                int quantity = entry.getValue();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(
                                "Product not found with id: " + productId));

                if (product.getStockQuantity() < quantity) {
                    throw new InsufficientStockException(
                            "Insufficient stock for '" + product.getName() + "' (SKU: "
                                    + product.getSku() + ")",
                            product.getStockQuantity(), quantity);
                }

                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);

                builder.addItem(product, quantity, product.getPrice());
                rawTotal += product.getPrice() * quantity;
            }

            double finalTotal = discountStrategy.applyDiscount(rawTotal); // R9: Strategy
            Order order = builder.totalAmount(finalTotal).build();        // R9: Builder
            return orderRepository.save(order);
        } finally {
            stockLock.unlock(); // R5: always release in finally
        }
    }

    /** R4: group all orders by their status. */
    public Map<OrderStatus, List<Order>> groupOrdersByStatus() {
        return orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus));
    }

    /** R4: filter delivered orders, then sum their totals. */
    public double calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
