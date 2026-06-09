package com.stockmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

/**
 * Request body for placing an order (R8). Validated with {@code @Valid} in the
 * controller (R10). The discount fields drive runtime Strategy selection (R9).
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "customerName": "Alice",
 *   "items": { "1": 2, "3": 1 },
 *   "discountType": "PERCENTAGE",
 *   "discountValue": 10
 * }
 * </pre>
 */
public class OrderRequest {

    @NotBlank(message = "customerName must not be blank")
    private String customerName;

    /** Map of productId -> quantity. Demonstrates R1 (Map) at the API boundary. */
    @NotEmpty(message = "items must contain at least one product")
    private Map<Long, Integer> items;

    /** One of: NONE, PERCENTAGE, FLAT. Defaults to NONE when absent. */
    private String discountType = "NONE";

    /** Percentage (0-100) or flat amount, depending on discountType. */
    private double discountValue;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Map<Long, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Long, Integer> items) {
        this.items = items;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }
}
