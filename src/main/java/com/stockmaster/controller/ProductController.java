package com.stockmaster.controller;

import com.stockmaster.dto.PagedResult;
import com.stockmaster.dto.ProductRequest;
import com.stockmaster.dto.StockReportDto;
import com.stockmaster.entity.Product;
import com.stockmaster.jdbc.ReportingRepository;
import com.stockmaster.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST API for products (R8). Demonstrates the full set of HTTP verbs plus
 * endpoints that surface the Stream/JDBC features for the live demo.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ReportingRepository reportingRepository;

    public ProductController(ProductService productService,
                             ReportingRepository reportingRepository) {
        this.productService = productService;
        this.reportingRepository = reportingRepository;
    }

    @GetMapping
    public ResponseEntity<PagedResult<Product>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getAllProductsPaged(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<Product>> getByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id,
                                               @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /** R3/R4 demo: products grouped by category via Stream groupingBy + TreeMap. */
    @GetMapping("/grouped")
    public ResponseEntity<Map<String, List<Product>>> getGroupedByCategory() {
        return ResponseEntity.ok(productService.groupByCategory());
    }

    /** R4 demo: total inventory value via mapToDouble + reduce. */
    @GetMapping("/inventory-value")
    public ResponseEntity<Double> getInventoryValue() {
        return ResponseEntity.ok(productService.calculateTotalInventoryValue());
    }

    /** R6 demo: low-stock report sourced from a direct JDBC join query. */
    @GetMapping("/low-stock-report")
    public ResponseEntity<List<StockReportDto>> getLowStockReport(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(reportingRepository.getLowStockReport(threshold));
    }
}
