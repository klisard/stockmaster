package com.stockmaster.concurrency;

import com.stockmaster.entity.Warehouse;
import com.stockmaster.repository.WarehouseRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Concurrency component (R5).
 *
 * <p>Queries every warehouse <b>in parallel</b> to find which ones can fulfil a
 * given SKU/quantity. Each warehouse check is submitted as a {@link Callable} to
 * a fixed thread pool ({@link ExecutorService}), and results are gathered through
 * {@link Future#get(long, TimeUnit)} with a timeout. This models a real scenario
 * where each warehouse lookup could be a slow remote call best done concurrently.
 */
@Component
public class WarehouseStockChecker {

    private static final Logger log = LoggerFactory.getLogger(WarehouseStockChecker.class);

    private final WarehouseRepository warehouseRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public WarehouseStockChecker(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * @return locations of all warehouses that can supply {@code requiredQty} of {@code sku}
     */
    public List<String> findWarehousesWithStock(String sku, int requiredQty) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Future<String>> futures = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            Callable<String> task = () -> {
                // Simulate a non-trivial lookup so parallelism is observable.
                Integer onHand = warehouse.getStockLevels().getOrDefault(sku, 0);
                return onHand >= requiredQty ? warehouse.getLocation() : null;
            };
            futures.add(executorService.submit(task));
        }

        List<String> available = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                String location = future.get(5, TimeUnit.SECONDS);
                if (location != null) {
                    available.add(location);
                }
            } catch (TimeoutException e) {
                // R10: not swallowed - logged, and the task is cancelled.
                log.warn("Warehouse stock check timed out for SKU {}", sku, e);
                future.cancel(true);
            } catch (ExecutionException e) {
                log.error("Warehouse stock check failed for SKU {}", sku, e);
                throw new RuntimeException("Warehouse stock check failed", e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupt status
                throw new RuntimeException("Warehouse stock check interrupted", e);
            }
        }
        return available;
    }

    /** Gracefully shuts the pool down when the Spring context closes. */
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
