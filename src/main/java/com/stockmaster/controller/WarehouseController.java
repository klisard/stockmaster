package com.stockmaster.controller;

import com.stockmaster.concurrency.WarehouseStockChecker;
import com.stockmaster.entity.Warehouse;
import com.stockmaster.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API for warehouses (R8). The {@code /stock-check} endpoint triggers the
 * parallel {@link WarehouseStockChecker} (R5).
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseStockChecker warehouseStockChecker;

    public WarehouseController(WarehouseService warehouseService,
                               WarehouseStockChecker warehouseStockChecker) {
        this.warehouseService = warehouseService;
        this.warehouseStockChecker = warehouseStockChecker;
    }

    @GetMapping
    public ResponseEntity<List<Warehouse>> getAll() {
        return ResponseEntity.ok(warehouseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Warehouse> create(@RequestBody Warehouse warehouse) {
        Warehouse created = warehouseService.create(warehouse);
        return ResponseEntity.created(URI.create("/api/warehouses/" + created.getId())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** R5 demo: parallel stock check across all warehouses using an ExecutorService. */
    @GetMapping("/stock-check")
    public ResponseEntity<List<String>> checkStock(@RequestParam String sku,
                                                   @RequestParam int qty) {
        return ResponseEntity.ok(warehouseStockChecker.findWarehousesWithStock(sku, qty));
    }
}
