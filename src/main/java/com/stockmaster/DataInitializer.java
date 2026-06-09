package com.stockmaster;

import com.stockmaster.entity.Product;
import com.stockmaster.entity.Supplier;
import com.stockmaster.entity.Warehouse;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SupplierRepository;
import com.stockmaster.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds sample data on first startup (skips if suppliers table is already populated).
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public DataInitializer(SupplierRepository supplierRepository,
                           ProductRepository productRepository,
                           WarehouseRepository warehouseRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (supplierRepository.count() > 0) {
            log.info("DataInitializer: data already present, skipping seed.");
            return;
        }
        log.info("DataInitializer: seeding sample data...");

        Supplier techCorp = supplierRepository.save(
                new Supplier("TechCorp Supplies", "orders@techcorp.com", "+1-555-0100"));
        Supplier homeEss = supplierRepository.save(
                new Supplier("Home Essentials Ltd", "contact@homeessentials.com", "+1-555-0200"));
        Supplier officePro = supplierRepository.save(
                new Supplier("OfficePro Wholesale", "wholesale@officepro.com", "+1-555-0300"));

        Product hub = new Product("USB-C Hub 7-Port", "USBC-HUB-01", 49.99, "Electronics", 25);
        hub.setSupplier(techCorp);

        Product headset = new Product("Wireless Headset Pro", "WH-PRO-200", 89.99, "Electronics", 8);
        headset.setSupplier(techCorp);

        Product webcam = new Product("4K Webcam HD", "CAM-4K-300", 129.99, "Electronics", 15);
        webcam.setSupplier(techCorp);

        Product chair = new Product("Ergonomic Office Chair", "CHAIR-ERG-10", 349.99, "Furniture", 5);
        chair.setSupplier(homeEss);

        Product desk = new Product("Standing Desk 140cm", "DESK-STD-60", 499.99, "Furniture", 12);
        desk.setSupplier(homeEss);

        Product notebook = new Product("A4 Spiral Notebook", "NB-A4-SPIRAL", 4.99, "Stationery", 200);
        notebook.setSupplier(officePro);

        Product pen = new Product("Ballpoint Pen Set 10pk", "PEN-BP-10", 6.99, "Stationery", 350);
        pen.setSupplier(officePro);

        Product monitor = new Product("27\" 4K Monitor", "MON-27-4K", 399.99, "Electronics", 9);
        monitor.setSupplier(techCorp);

        productRepository.saveAll(List.of(hub, headset, webcam, chair, desk, notebook, pen, monitor));

        Warehouse nyc = new Warehouse("NYC Main Warehouse");
        nyc.getStockLevels().put("USBC-HUB-01", 15);
        nyc.getStockLevels().put("WH-PRO-200", 5);
        nyc.getStockLevels().put("CAM-4K-300", 8);
        nyc.getStockLevels().put("CHAIR-ERG-10", 3);
        nyc.getStockLevels().put("NB-A4-SPIRAL", 100);

        Warehouse la = new Warehouse("LA Distribution Center");
        la.getStockLevels().put("USBC-HUB-01", 10);
        la.getStockLevels().put("DESK-STD-60", 6);
        la.getStockLevels().put("PEN-BP-10", 200);
        la.getStockLevels().put("MON-27-4K", 4);
        la.getStockLevels().put("WH-PRO-200", 3);

        warehouseRepository.saveAll(List.of(nyc, la));

        log.info("DataInitializer: seeded {} suppliers, {} products, {} warehouses.",
                3, 8, 2);
    }
}
