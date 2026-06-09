package com.stockmaster.entity;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A physical warehouse holding stock for multiple SKUs.
 *
 * <p>{@code stockLevels} is a {@link HashMap} (R1) mapping SKU to on-hand
 * quantity, persisted to a dedicated {@code warehouse_stock} join table via
 * {@code @ElementCollection} (R7). HashMap is chosen for O(1) SKU lookups
 * during the parallel stock check performed by the concurrency component (R5).
 */
@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "warehouse_stock",
            joinColumns = @JoinColumn(name = "warehouse_id"))
    @MapKeyColumn(name = "sku")
    @Column(name = "quantity")
    private Map<String, Integer> stockLevels = new HashMap<>();

    /** No-arg constructor required by the JPA specification. */
    public Warehouse() {
    }

    public Warehouse(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, Integer> getStockLevels() {
        return stockLevels;
    }

    public void setStockLevels(Map<String, Integer> stockLevels) {
        this.stockLevels = stockLevels;
    }
}
