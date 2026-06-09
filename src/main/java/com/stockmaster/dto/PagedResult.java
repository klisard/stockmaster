package com.stockmaster.dto;

import java.util.List;

/**
 * Generic, type-safe pagination wrapper (R2: Generics).
 *
 * <p>Wraps any page of results {@code <T>} together with pagination metadata,
 * so REST endpoints can return {@code PagedResult<Product>},
 * {@code PagedResult<Order>}, etc. without duplicating the envelope.
 *
 * @param <T> the type of the elements in this page
 */
public class PagedResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PagedResult(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return page >= totalPages - 1;
    }
}
