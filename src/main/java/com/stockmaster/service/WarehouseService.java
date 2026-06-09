package com.stockmaster.service;

import com.stockmaster.entity.Warehouse;
import com.stockmaster.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for warehouses (CRUD support for warehouse REST endpoints).
 */
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));
    }

    @Transactional
    public Warehouse create(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }
}
