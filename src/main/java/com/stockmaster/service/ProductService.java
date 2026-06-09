package com.stockmaster.service;

import com.stockmaster.dto.PagedResult;
import com.stockmaster.dto.ProductRequest;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.Supplier;
import com.stockmaster.exception.DuplicateSkuException;
import com.stockmaster.exception.ProductNotFoundException;
import com.stockmaster.pattern.observer.StockEventPublisher;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Business logic for products. Concentrates the functional-programming and
 * Stream-API requirements:
 * <ul>
 *   <li>R3 - {@link Predicate}, {@link Function}, {@link Comparator}</li>
 *   <li>R4 - filter / map / sorted / collect / groupingBy / mapToDouble / reduce</li>
 *   <li>R1 - ArrayList and TreeMap, chosen by use case</li>
 *   <li>R9 - publishes low-stock events through the Observer subject</li>
 * </ul>
 */
@Service
public class ProductService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockEventPublisher stockEventPublisher;

    public ProductService(ProductRepository productRepository,
                          SupplierRepository supplierRepository,
                          StockEventPublisher stockEventPublisher) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockEventPublisher = stockEventPublisher;
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    /**
     * R3: accepts a caller-supplied {@link Predicate} and R4: applies it with a
     * Stream filter. Lets callers express arbitrary filtering criteria.
     */
    public List<Product> findByPredicate(Predicate<Product> filter) {
        return productRepository.findAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /** Convenience wrapper over {@link #findByPredicate} for low-stock items. */
    public List<Product> findLowStock() {
        return findByPredicate(p -> p.getStockQuantity() < LOW_STOCK_THRESHOLD);
    }

    /**
     * R4: groupingBy into a {@link TreeMap} (R1) so categories come back sorted
     * alphabetically - a HashMap would give nondeterministic ordering.
     */
    public TreeMap<String, List<Product>> groupByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory() : "Uncategorised",
                        TreeMap::new,
                        Collectors.toList()));
    }

    /** R4: mapToDouble + reduce to compute the total value of all inventory. */
    public double calculateTotalInventoryValue() {
        return productRepository.findAll().stream()
                .mapToDouble(p -> p.getPrice() * p.getStockQuantity())
                .reduce(0.0, Double::sum);
    }

    /**
     * R2 + R3: a generic method that maps every product through a caller-supplied
     * {@link Function}, returning a list of whatever type the function produces.
     */
    public <R> List<R> mapProducts(Function<Product, R> mapper) {
        return productRepository.findAll().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * R4: sorted by name (R3: Comparator), then sliced into the requested page
     * and wrapped in the generic {@link PagedResult} (R2).
     */
    public PagedResult<Product> getAllProductsPaged(int page, int size) {
        List<Product> all = productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());

        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<Product> pageContent = new ArrayList<>(all.subList(from, to));
        return new PagedResult<>(pageContent, page, size, all.size());
    }

    /** R1: explicitly returns an ArrayList (ordered, index-accessible). */
    public ArrayList<Product> findByCategory(String category) {
        return new ArrayList<>(productRepository.findByCategory(category));
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        productRepository.findBySku(request.getSku()).ifPresent(existing -> {
            throw new DuplicateSkuException("SKU already exists: " + request.getSku());
        });
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Supplier not found with id: " + request.getSupplierId()));
        Product product = new Product(request.getName(), request.getSku(),
                request.getPrice(), request.getCategory(), request.getStockQuantity());
        product.setSupplier(supplier);
        Product saved = productRepository.save(product);
        if (saved.getStockQuantity() < LOW_STOCK_THRESHOLD) {
            stockEventPublisher.notifyLowStock(saved); // R9: Observer
        }
        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = findById(id);
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Supplier not found with id: " + request.getSupplierId()));
        if (!product.getSku().equals(request.getSku())) {
            productRepository.findBySku(request.getSku()).ifPresent(existing -> {
                throw new DuplicateSkuException("SKU already exists: " + request.getSku());
            });
        }
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setSupplier(supplier);
        Product saved = productRepository.save(product);
        if (saved.getStockQuantity() < LOW_STOCK_THRESHOLD) {
            stockEventPublisher.notifyLowStock(saved);
        }
        return saved;
    }

    @Transactional
    public Product updateStock(Long id, int newQuantity) {
        Product product = findById(id);
        product.setStockQuantity(newQuantity);
        Product saved = productRepository.save(product);
        if (saved.getStockQuantity() < LOW_STOCK_THRESHOLD) {
            stockEventPublisher.notifyLowStock(saved); // R9: Observer
        }
        return saved;
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
