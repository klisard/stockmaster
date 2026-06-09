package com.stockmaster.service;

import com.stockmaster.entity.Supplier;
import com.stockmaster.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for suppliers (CRUD support for the supplier REST endpoints).
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
    }

    @Transactional
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier update(Long id, Supplier updates) {
        Supplier existing = findById(id);
        existing.setName(updates.getName());
        existing.setContactEmail(updates.getContactEmail());
        existing.setPhone(updates.getPhone());
        return supplierRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
