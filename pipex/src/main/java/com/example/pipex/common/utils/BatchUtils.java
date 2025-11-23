package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Batch processing utilities với enterprise-grade features
 * - Partition large datasets
 * - Batch execute operations
 * - Progress tracking
 * - Error recovery for partial failures
 */
@Slf4j
public final class BatchUtils {

    private BatchUtils() {
        // Utility class
    }

    // ========== PARTITIONING ==========

    /**
     * Partition collection into chunks
     */
    public static <T> List<List<T>> partition(Collection<T> collection, int batchSize) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be positive");
        }

        List<T> list = new ArrayList<>(collection);
        List<List<T>> partitions = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            partitions.add(list.subList(i, end));
        }

        log.debug("Partitioned collection into {} batches of size {}", partitions.size(), batchSize);
        return partitions;
    }

    /**
     * Partition collection into chunks with overlap
     */
    public static <T> List<List<T>> partitionWithOverlap(Collection<T> collection, int batchSize, int overlap) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        if (batchSize <= 0 || overlap < 0 || overlap >= batchSize) {
            throw new IllegalArgumentException("Invalid batch size or overlap");
        }

        List<T> list = new ArrayList<>(collection);
        List<List<T>> partitions = new ArrayList<>();

        int start = 0;
        while (start < list.size()) {
            int end = Math.min(start + batchSize, list.size());
            partitions.add(list.subList(start, end));
            start += batchSize - overlap;
        }

        log.debug("Partitioned collection into {} batches with overlap {}", partitions.size(), overlap);
        return partitions;
    }

    /**
     * Partition collection into chunks by predicate
     */
    public static <T> List<List<T>> partitionByPredicate(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> trueList = new ArrayList<>();
        List<T> falseList = new ArrayList<>();

        for (T item : collection) {
            if (predicate.test(item)) {
                trueList.add(item);
            } else {
                falseList.add(item);
            }
        }

        List<List<T>> partitions = new ArrayList<>();
        if (!trueList.isEmpty())
            partitions.add(trueList);
        if (!falseList.isEmpty())
            partitions.add(falseList);

        log.debug("Partitioned collection by predicate into {} groups", partitions.size());
        return partitions;
    }

    // ========== BATCH PROCESSING ==========

    /**
     * Process collection in batches
     */
    public static <T> void processBatches(Collection<T> collection, int batchSize, Consumer<List<T>> processor) {
        if (collection == null || collection.isEmpty()) {
            return;
        }

        List<List<T>> batches = partition(collection, batchSize);
        log.info("Processing {} items in {} batches", collection.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Processing batch {}/{} with {} items", i + 1, batches.size(), batch.size());
                processor.accept(batch);
                log.debug("Batch {}/{} completed successfully", i + 1, batches.size());
            } catch (Exception e) {
                log.error("Batch {}/{} failed: {}", i + 1, batches.size(), e.getMessage(), e);
                throw e;
            }
        }

        log.info("All batches processed successfully");
    }

    /**
     * Process collection in batches with error handling
     */
    public static <T> BatchProcessingResult<T> processBatchesWithErrorHandling(Collection<T> collection, int batchSize,
            Consumer<List<T>> processor) {
        if (collection == null || collection.isEmpty()) {
            return new BatchProcessingResult<>(Collections.emptyList(), Collections.emptyList());
        }

        List<List<T>> batches = partition(collection, batchSize);
        List<T> processedItems = new ArrayList<>();
        List<BatchError<T>> errors = new ArrayList<>();

        log.info("Processing {} items in {} batches with error handling", collection.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Processing batch {}/{} with {} items", i + 1, batches.size(), batch.size());
                processor.accept(batch);
                processedItems.addAll(batch);
                log.debug("Batch {}/{} completed successfully", i + 1, batches.size());
            } catch (Exception e) {
                log.error("Batch {}/{} failed: {}", i + 1, batches.size(), e.getMessage(), e);
                errors.add(new BatchError<>(batch, e, i));
            }
        }

        log.info("Batch processing completed: {} items processed, {} batches failed",
                processedItems.size(), errors.size());

        return new BatchProcessingResult<>(processedItems, errors);
    }

    /**
     * Process collection in batches with progress callback
     */
    public static <T> void processBatchesWithProgress(Collection<T> collection, int batchSize,
            Consumer<List<T>> processor,
            Consumer<ProgressInfo> progressCallback) {
        if (collection == null || collection.isEmpty()) {
            return;
        }

        List<List<T>> batches = partition(collection, batchSize);
        int totalBatches = batches.size();
        int totalItems = collection.size();

        log.info("Processing {} items in {} batches with progress tracking", totalItems, totalBatches);

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Processing batch {}/{} with {} items", i + 1, totalBatches, batch.size());
                processor.accept(batch);

                ProgressInfo progress = new ProgressInfo(i + 1, totalBatches, batch.size(), totalItems);
                progressCallback.accept(progress);

                log.debug("Batch {}/{} completed successfully", i + 1, totalBatches);
            } catch (Exception e) {
                log.error("Batch {}/{} failed: {}", i + 1, totalBatches, e.getMessage(), e);
                throw e;
            }
        }

        log.info("All batches processed successfully");
    }

    // ========== BATCH TRANSFORMATION ==========

    /**
     * Transform collection in batches
     */
    public static <T, R> List<R> transformBatches(Collection<T> collection, int batchSize,
            Function<List<T>, List<R>> transformer) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> batches = partition(collection, batchSize);
        List<R> results = new ArrayList<>();

        log.info("Transforming {} items in {} batches", collection.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Transforming batch {}/{} with {} items", i + 1, batches.size(), batch.size());
                List<R> transformed = transformer.apply(batch);
                results.addAll(transformed);
                log.debug("Batch {}/{} transformed successfully", i + 1, batches.size());
            } catch (Exception e) {
                log.error("Batch {}/{} transformation failed: {}", i + 1, batches.size(), e.getMessage(), e);
                throw e;
            }
        }

        log.info("All batches transformed successfully, {} results generated", results.size());
        return results;
    }

    /**
     * Transform collection in batches with error handling
     */
    public static <T, R> BatchTransformationResult<T, R> transformBatchesWithErrorHandling(Collection<T> collection,
            int batchSize, Function<List<T>, List<R>> transformer) {
        if (collection == null || collection.isEmpty()) {
            return new BatchTransformationResult<>(Collections.emptyList(), Collections.emptyList());
        }

        List<List<T>> batches = partition(collection, batchSize);
        List<R> results = new ArrayList<>();
        List<BatchError<T>> errors = new ArrayList<>();

        log.info("Transforming {} items in {} batches with error handling", collection.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Transforming batch {}/{} with {} items", i + 1, batches.size(), batch.size());
                List<R> transformed = transformer.apply(batch);
                results.addAll(transformed);
                log.debug("Batch {}/{} transformed successfully", i + 1, batches.size());
            } catch (Exception e) {
                log.error("Batch {}/{} transformation failed: {}", i + 1, batches.size(), e.getMessage(), e);
                errors.add(new BatchError<>(batch, e, i));
            }
        }

        log.info("Batch transformation completed: {} results generated, {} batches failed",
                results.size(), errors.size());

        return new BatchTransformationResult<>(results, errors);
    }

    // ========== BATCH FILTERING ==========

    /**
     * Filter collection in batches
     */
    public static <T> List<T> filterBatches(Collection<T> collection, int batchSize, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> batches = partition(collection, batchSize);
        List<T> results = new ArrayList<>();

        log.info("Filtering {} items in {} batches", collection.size(), batches.size());

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            try {
                log.debug("Filtering batch {}/{} with {} items", i + 1, batches.size(), batch.size());
                List<T> filtered = batch.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
                results.addAll(filtered);
                log.debug("Batch {}/{} filtered successfully", i + 1, batches.size());
            } catch (Exception e) {
                log.error("Batch {}/{} filtering failed: {}", i + 1, batches.size(), e.getMessage(), e);
                throw e;
            }
        }

        log.info("All batches filtered successfully, {} items match criteria", results.size());
        return results;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Calculate optimal batch size
     */
    public static int calculateOptimalBatchSize(int totalItems, int maxBatches, int minBatchSize) {
        if (totalItems <= 0 || maxBatches <= 0 || minBatchSize <= 0) {
            throw new IllegalArgumentException("Invalid parameters for batch size calculation");
        }

        int optimalSize = Math.max(minBatchSize, totalItems / maxBatches);
        return Math.min(optimalSize, totalItems);
    }

    /**
     * Calculate number of batches
     */
    public static int calculateBatchCount(int totalItems, int batchSize) {
        if (totalItems <= 0 || batchSize <= 0) {
            return 0;
        }
        return (totalItems + batchSize - 1) / batchSize; // Ceiling division
    }

    /**
     * Check if batch size is optimal
     */
    public static boolean isOptimalBatchSize(int totalItems, int batchSize, int maxBatches) {
        int batchCount = calculateBatchCount(totalItems, batchSize);
        return batchCount <= maxBatches;
    }

    // ========== RESULT CLASSES ==========

    /**
     * Result class for batch processing with error handling
     */
    public static class BatchProcessingResult<T> {
        private final List<T> processedItems;
        private final List<BatchError<T>> errors;

        public BatchProcessingResult(List<T> processedItems, List<BatchError<T>> errors) {
            this.processedItems = processedItems != null ? processedItems : Collections.emptyList();
            this.errors = errors != null ? errors : Collections.emptyList();
        }

        public List<T> getProcessedItems() {
            return processedItems;
        }

        public List<BatchError<T>> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean isSuccess() {
            return errors.isEmpty();
        }

        public int getProcessedCount() {
            return processedItems.size();
        }

        public int getErrorCount() {
            return errors.size();
        }

        public int getTotalCount() {
            return getProcessedCount() + getErrorCount();
        }

        public double getSuccessRate() {
            int total = getTotalCount();
            return total > 0 ? (double) getProcessedCount() / total : 0.0;
        }
    }

    /**
     * Result class for batch transformation with error handling
     */
    public static class BatchTransformationResult<T, R> {
        private final List<R> results;
        private final List<BatchError<T>> errors;

        public BatchTransformationResult(List<R> results, List<BatchError<T>> errors) {
            this.results = results != null ? results : Collections.emptyList();
            this.errors = errors != null ? errors : Collections.emptyList();
        }

        public List<R> getResults() {
            return results;
        }

        public List<BatchError<T>> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean isSuccess() {
            return errors.isEmpty();
        }

        public int getResultCount() {
            return results.size();
        }

        public int getErrorCount() {
            return errors.size();
        }

        public double getSuccessRate() {
            int total = getResultCount() + getErrorCount();
            return total > 0 ? (double) getResultCount() / total : 0.0;
        }
    }

    /**
     * Error class for batch operations
     */
    public static class BatchError<T> {
        private final List<T> failedItems;
        private final Exception exception;
        private final int batchIndex;

        public BatchError(List<T> failedItems, Exception exception, int batchIndex) {
            this.failedItems = failedItems;
            this.exception = exception;
            this.batchIndex = batchIndex;
        }

        public List<T> getFailedItems() {
            return failedItems;
        }

        public Exception getException() {
            return exception;
        }

        public int getBatchIndex() {
            return batchIndex;
        }

        @Override
        public String toString() {
            return String.format("BatchError{batchIndex=%d, failedItems=%d, exception=%s}",
                    batchIndex, failedItems.size(), exception.getMessage());
        }
    }

    /**
     * Progress information class
     */
    public static class ProgressInfo {
        private final int currentBatch;
        private final int totalBatches;
        private final int currentBatchSize;
        private final int totalItems;

        public ProgressInfo(int currentBatch, int totalBatches, int currentBatchSize, int totalItems) {
            this.currentBatch = currentBatch;
            this.totalBatches = totalBatches;
            this.currentBatchSize = currentBatchSize;
            this.totalItems = totalItems;
        }

        public int getCurrentBatch() {
            return currentBatch;
        }

        public int getTotalBatches() {
            return totalBatches;
        }

        public int getCurrentBatchSize() {
            return currentBatchSize;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public double getProgressPercentage() {
            return totalBatches > 0 ? (double) currentBatch / totalBatches * 100 : 0.0;
        }

        public boolean isComplete() {
            return currentBatch >= totalBatches;
        }

        @Override
        public String toString() {
            return String.format("ProgressInfo{batch=%d/%d, items=%d, progress=%.1f%%}",
                    currentBatch, totalBatches, totalItems, getProgressPercentage());
        }
    }
}
