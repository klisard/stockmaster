package com.stockmaster.controller;

import com.stockmaster.dto.OrderRequest;
import com.stockmaster.entity.Order;
import com.stockmaster.enums.OrderStatus;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.jdbc.ReportingRepository;
import com.stockmaster.pattern.strategy.DiscountStrategy;
import com.stockmaster.pattern.strategy.FlatDiscountStrategy;
import com.stockmaster.pattern.strategy.NoDiscountStrategy;
import com.stockmaster.pattern.strategy.PercentageDiscountStrategy;
import com.stockmaster.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST API for orders (R8). The {@code placeOrder} endpoint selects a discount
 * {@link DiscountStrategy} at runtime (R9) and declares the checked
 * {@link InsufficientStockException} (R10), which the global handler maps to 409.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ReportingRepository reportingRepository;

    public OrderController(OrderService orderService, ReportingRepository reportingRepository) {
        this.orderService = orderService;
        this.reportingRepository = reportingRepository;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request)
            throws InsufficientStockException {

        DiscountStrategy strategy = resolveStrategy(request); // R9: runtime selection
        Order order = orderService.placeOrder(
                request.getCustomerName(), request.getItems(), strategy);
        return ResponseEntity.created(URI.create("/api/orders/" + order.getId())).body(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id,
                                              @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    /** R4 demo: orders grouped by status. */
    @GetMapping("/grouped")
    public ResponseEntity<Map<OrderStatus, List<Order>>> getGroupedByStatus() {
        return ResponseEntity.ok(orderService.groupOrdersByStatus());
    }

    /** R4 demo: total revenue from delivered orders (filter + sum). */
    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenue() {
        return ResponseEntity.ok(orderService.calculateTotalRevenue());
    }

    /** R6 demo: revenue grouped by status from a direct JDBC aggregate query. */
    @GetMapping("/revenue-report")
    public ResponseEntity<List<String>> getRevenueReport() {
        return ResponseEntity.ok(reportingRepository.getRevenueByStatus());
    }

    /** Maps the request's discountType to a concrete Strategy (R9). */
    private DiscountStrategy resolveStrategy(OrderRequest request) {
        String type = request.getDiscountType() == null ? "NONE" : request.getDiscountType();
        return switch (type.toUpperCase()) {
            case "PERCENTAGE" -> new PercentageDiscountStrategy(request.getDiscountValue());
            case "FLAT" -> new FlatDiscountStrategy(request.getDiscountValue());
            default -> new NoDiscountStrategy();
        };
    }
}
