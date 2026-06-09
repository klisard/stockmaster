package com.stockmaster.repository;

import com.stockmaster.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Product} (R7).
 *
 * <p>Extending {@code JpaRepository} gives full CRUD for free. Derived query
 * methods (parsed from their names) and explicit JPQL {@code @Query} methods
 * cover the domain-specific lookups.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByStockQuantityLessThan(int threshold);

    /** JPQL with named parameters - the exact pattern taught in Week 9. */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("min") double min, @Param("max") double max);

    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId")
    List<Product> findBySupplierId(@Param("supplierId") Long supplierId);
}
