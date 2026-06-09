package com.stockmaster.repository;

import com.stockmaster.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Warehouse} (R7).
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
