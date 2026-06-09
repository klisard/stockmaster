package com.stockmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * A product held in inventory.
 *
 * <p>Owning side of the many-to-one relationship with {@link Supplier} - the
 * {@code supplier_id} foreign key column lives on this table. Demonstrates R7.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    /** Owning side: holds the foreign key to the supplier. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnoreProperties({"products", "hibernateLazyInitializer"})
    private Supplier supplier;

    /** No-arg constructor required by the JPA specification. */
    public Product() {
    }

    public Product(String name, String sku, double price, String category, int stockQuantity) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
