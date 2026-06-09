package com.stockmaster.jdbc;

import com.stockmaster.dto.StockReportDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC reporting (R6), deliberately separate from the JPA layer.
 *
 * <p>Multi-table JOINs and GROUP BY aggregates are expressed more naturally in
 * raw SQL than in JPQL. This repository borrows the same HikariCP {@link DataSource}
 * that JPA uses (injected by Spring), so there is only one connection pool.
 *
 * <p>Demonstrates the Week 8 patterns: {@code PreparedStatement} to prevent SQL
 * injection, try-with-resources for automatic {@code Connection}/{@code Statement}/
 * {@code ResultSet} closing, and meaningful {@code SQLException} handling.
 */
@Repository
public class ReportingRepository {

    private final DataSource dataSource;

    public ReportingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Report 1: products below a stock threshold, joined to their supplier name. */
    public List<StockReportDto> getLowStockReport(int threshold) {
        String sql = """
                SELECT p.id, p.name, p.sku, p.stock_quantity, p.category,
                       s.name AS supplier_name
                FROM products p
                JOIN suppliers s ON p.supplier_id = s.id
                WHERE p.stock_quantity < ?
                ORDER BY p.stock_quantity ASC
                """;

        List<StockReportDto> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold); // bound parameter - no SQL injection

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockReportDto dto = new StockReportDto();
                    dto.setProductId(rs.getLong("id"));
                    dto.setProductName(rs.getString("name"));
                    dto.setSku(rs.getString("sku"));
                    dto.setStockQuantity(rs.getInt("stock_quantity"));
                    dto.setCategory(rs.getString("category"));
                    dto.setSupplierName(rs.getString("supplier_name"));
                    results.add(dto);
                }
            }
        } catch (SQLException e) {
            // R10: never swallowed - rethrown with diagnostic SQLState/error code.
            throw new RuntimeException("JDBC low-stock report failed. SQLState="
                    + e.getSQLState() + ", code=" + e.getErrorCode(), e);
        }
        return results;
    }

    /** Report 2: order count and revenue grouped by status (SQL aggregate). */
    public List<String> getRevenueByStatus() {
        String sql = """
                SELECT status, COUNT(*) AS order_count, COALESCE(SUM(total_amount), 0) AS revenue
                FROM orders
                GROUP BY status
                ORDER BY revenue DESC
                """;

        List<String> report = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                report.add(String.format("Status: %-12s | Orders: %3d | Revenue: %.2f",
                        rs.getString("status"),
                        rs.getInt("order_count"),
                        rs.getDouble("revenue")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("JDBC revenue report failed. SQLState="
                    + e.getSQLState() + ", code=" + e.getErrorCode(), e);
        }
        return report;
    }
}
