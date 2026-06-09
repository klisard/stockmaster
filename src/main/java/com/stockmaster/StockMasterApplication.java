package com.stockmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the StockMaster inventory and order management system.
 *
 * <p>{@code @SpringBootApplication} is a convenience annotation that combines:
 * <ul>
 *   <li>{@code @Configuration} - marks this class as a source of bean definitions</li>
 *   <li>{@code @EnableAutoConfiguration} - lets Spring Boot auto-configure the
 *       embedded Tomcat server, JPA, the HikariCP datasource, etc.</li>
 *   <li>{@code @ComponentScan} - scans the {@code com.stockmaster} package and all
 *       sub-packages for {@code @Component}, {@code @Service}, {@code @Repository}
 *       and {@code @RestController} beans.</li>
 * </ul>
 */
@SpringBootApplication
public class StockMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMasterApplication.class, args);
    }
}
