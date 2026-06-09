package com.stockmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request DTO for creating a product. Uses supplierId instead of a raw Supplier
 * entity to avoid "detached entity passed to persist" on the JPA side.
 */
public class ProductRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "sku must not be blank")
    private String sku;

    @Positive(message = "price must be positive")
    private double price;

    private String category;

    @PositiveOrZero(message = "stockQuantity must be zero or positive")
    private int stockQuantity;

    @NotNull(message = "supplierId is required")
    private Long supplierId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}
