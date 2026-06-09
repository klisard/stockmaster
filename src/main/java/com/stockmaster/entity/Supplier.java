package com.stockmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A supplier that provides products to the store.
 *
 * <p>Owns the inverse side of a one-to-many relationship with {@link Product}
 * ({@code mappedBy = "supplier"}). Demonstrates R7 (JPA/ORM).
 */
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "contact_email", nullable = false, unique = true, length = 150)
    private String contactEmail;

    @Column(name = "phone", length = 30)
    private String phone;

    /**
     * Inverse side of the relationship. The foreign key lives on the Product table.
     * {@code @JsonIgnoreProperties} breaks the bidirectional serialization cycle.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"supplier", "hibernateLazyInitializer"})
    private List<Product> products = new ArrayList<>();

    /** No-arg constructor required by the JPA specification. */
    public Supplier() {
    }

    public Supplier(String name, String contactEmail, String phone) {
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
