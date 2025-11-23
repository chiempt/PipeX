package com.example.pipex.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated data wrapper với enterprise-grade features
 * - Pagination metadata
 * - Content list
 * - Navigation information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedData<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int numberOfElements;

    // ========== UTILITY METHODS ==========

    /**
     * Check if page has content
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    /**
     * Get content size
     */
    public int getContentSize() {
        return content != null ? content.size() : 0;
    }

    /**
     * Check if page is empty
     */
    public boolean isEmpty() {
        return !hasContent();
    }

    /**
     * Check if page is not empty
     */
    public boolean isNotEmpty() {
        return hasContent();
    }

    /**
     * Get next page number
     */
    public Integer getNextPage() {
        return hasNext() ? page + 1 : null;
    }

    /**
     * Get previous page number
     */
    public Integer getPreviousPage() {
        return hasPrevious() ? page - 1 : null;
    }

    /**
     * Check if has next page
     */
    public boolean hasNext() {
        return !last;
    }

    /**
     * Check if has previous page
     */
    public boolean hasPrevious() {
        return !first;
    }

    /**
     * Get start element index
     */
    public long getStartElement() {
        return page * size + 1;
    }

    /**
     * Get end element index
     */
    public long getEndElement() {
        return page * size + numberOfElements;
    }

    /**
     * Get total pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Get total elements
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * Get page number
     */
    public int getPage() {
        return page;
    }

    /**
     * Get page size
     */
    public int getSize() {
        return size;
    }

    /**
     * Get number of elements in current page
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * Check if is first page
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Check if is last page
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Get content safely
     */
    public List<T> getContent() {
        return content != null ? content : List.of();
    }

    // ========== FACTORY METHODS ==========

    /**
     * Create paginated data from Spring Page
     */
    public static <T> PaginatedData<T> fromPage(org.springframework.data.domain.Page<T> page) {
        return PaginatedData.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

    /**
     * Create paginated data with custom values
     */
    public static <T> PaginatedData<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PaginatedData.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .numberOfElements(content != null ? content.size() : 0)
                .build();
    }

    /**
     * Create empty paginated data
     */
    public static <T> PaginatedData<T> empty() {
        return PaginatedData.<T>builder()
                .content(List.of())
                .page(0)
                .size(0)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .numberOfElements(0)
                .build();
    }

    /**
     * Create empty paginated data with page info
     */
    public static <T> PaginatedData<T> empty(int page, int size) {
        return PaginatedData.<T>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .numberOfElements(0)
                .build();
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        return String.format("PaginatedData{page=%d, size=%d, totalElements=%d, totalPages=%d, contentSize=%d}",
                page, size, totalElements, totalPages, getContentSize());
    }
}
