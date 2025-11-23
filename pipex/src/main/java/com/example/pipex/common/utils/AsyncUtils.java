package com.example.pipex.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Async utilities với enterprise-grade features
 * - Spring bean for async operations
 * - executeAsync(): Run tasks with CompletableFuture
 * - executeBatch(): Run multiple tasks in parallel
 * - executeWithTimeout(): Timeout support
 * - Error aggregation for batch operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncUtils {

    // ========== BASIC ASYNC OPERATIONS ==========

    /**
     * Execute task asynchronously
     */
    @Async("virtualThreadExecutor")
    public <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        try {
            log.debug("Executing async task");
            T result = task.get();
            log.debug("Async task completed successfully");
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async task failed: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Execute task asynchronously with custom executor
     */
    @Async("standardAsyncExecutor")
    public <T> CompletableFuture<T> executeAsync(Supplier<T> task, String executorName) {
        try {
            log.debug("Executing async task with executor: {}", executorName);
            T result = task.get();
            log.debug("Async task completed successfully with executor: {}", executorName);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async task failed with executor {}: {}", executorName, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Execute task asynchronously and return CompletableFuture
     */
    public <T> CompletableFuture<T> executeAsyncFuture(Supplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Executing async future task");
                T result = task.get();
                log.debug("Async future task completed successfully");
                return result;
            } catch (Exception e) {
                log.error("Async future task failed: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        });
    }

    // ========== BATCH OPERATIONS ==========

    /**
     * Execute multiple tasks in parallel
     */
    public <T> CompletableFuture<List<T>> executeBatch(List<Supplier<T>> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        log.debug("Executing batch of {} tasks", tasks.size());

        List<CompletableFuture<T>> futures = tasks.stream()
                .map(this::executeAsyncFuture)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    /**
     * Execute multiple tasks in parallel with error handling
     */
    public <T> CompletableFuture<BatchResult<T>> executeBatchWithErrorHandling(List<Supplier<T>> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(new BatchResult<>(new ArrayList<>(), new ArrayList<>()));
        }

        log.debug("Executing batch of {} tasks with error handling", tasks.size());

        List<CompletableFuture<T>> futures = tasks.stream()
                .map(task -> executeAsyncFuture(task)
                        .handle((result, throwable) -> {
                            if (throwable != null) {
                                log.warn("Task failed in batch: {}", throwable.getMessage());
                                return null;
                            }
                            return result;
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<T> results = new ArrayList<>();
                    List<Exception> errors = new ArrayList<>();

                    for (CompletableFuture<T> future : futures) {
                        try {
                            T result = future.join();
                            if (result != null) {
                                results.add(result);
                            }
                        } catch (Exception e) {
                            errors.add(e);
                        }
                    }

                    log.debug("Batch completed: {} successes, {} failures", results.size(), errors.size());
                    return new BatchResult<>(results, errors);
                });
    }

    /**
     * Execute multiple tasks in parallel with timeout
     */
    public <T> CompletableFuture<List<T>> executeBatchWithTimeout(List<Supplier<T>> tasks, long timeout,
            TimeUnit timeUnit) {
        if (tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        log.debug("Executing batch of {} tasks with timeout: {} {}", tasks.size(), timeout, timeUnit);

        List<CompletableFuture<T>> futures = tasks.stream()
                .map(this::executeAsyncFuture)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(timeout, timeUnit)
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    // ========== TIMEOUT OPERATIONS ==========

    /**
     * Execute task with timeout
     */
    public <T> CompletableFuture<T> executeWithTimeout(Supplier<T> task, long timeout, TimeUnit timeUnit) {
        log.debug("Executing task with timeout: {} {}", timeout, timeUnit);

        return CompletableFuture.supplyAsync(() -> {
            try {
                T result = task.get();
                log.debug("Task completed within timeout");
                return result;
            } catch (Exception e) {
                log.error("Task failed within timeout: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        }).orTimeout(timeout, timeUnit);
    }

    /**
     * Execute task with timeout and fallback
     */
    public <T> CompletableFuture<T> executeWithTimeoutAndFallback(Supplier<T> task, Supplier<T> fallback, long timeout,
            TimeUnit timeUnit) {
        log.debug("Executing task with timeout and fallback: {} {}", timeout, timeUnit);

        return CompletableFuture.supplyAsync(() -> {
            try {
                T result = task.get();
                log.debug("Task completed within timeout");
                return result;
            } catch (Exception e) {
                log.error("Task failed within timeout: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        }).orTimeout(timeout, timeUnit)
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        log.warn("Task timed out or failed, using fallback: {}", throwable.getMessage());
                        try {
                            return fallback.get();
                        } catch (Exception e) {
                            log.error("Fallback also failed: {}", e.getMessage(), e);
                            throw new CompletionException(e);
                        }
                    }
                    return result;
                });
    }

    // ========== TRANSFORMATION OPERATIONS ==========

    /**
     * Transform result asynchronously
     */
    public <T, R> CompletableFuture<R> transformAsync(CompletableFuture<T> future, Function<T, R> transformer) {
        return future.thenApplyAsync(result -> {
            try {
                log.debug("Transforming async result");
                R transformed = transformer.apply(result);
                log.debug("Async transformation completed");
                return transformed;
            } catch (Exception e) {
                log.error("Async transformation failed: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Transform result asynchronously with error handling
     */
    public <T, R> CompletableFuture<R> transformAsyncWithErrorHandling(CompletableFuture<T> future,
            Function<T, R> transformer, Function<Exception, R> errorHandler) {
        return future.handleAsync((result, throwable) -> {
            if (throwable != null) {
                log.warn("Transformation failed, using error handler: {}", throwable.getMessage());
                try {
                    return errorHandler.apply((Exception) throwable);
                } catch (Exception e) {
                    log.error("Error handler also failed: {}", e.getMessage(), e);
                    throw new CompletionException(e);
                }
            }
            try {
                log.debug("Transforming async result");
                R transformed = transformer.apply(result);
                log.debug("Async transformation completed");
                return transformed;
            } catch (Exception e) {
                log.error("Async transformation failed: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        });
    }

    // ========== UTILITY METHODS ==========

    /**
     * Wait for all futures to complete
     */
    public <T> CompletableFuture<List<T>> waitForAll(List<CompletableFuture<T>> futures) {
        if (futures == null || futures.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    /**
     * Wait for any future to complete
     */
    public <T> CompletableFuture<T> waitForAny(List<CompletableFuture<T>> futures) {
        if (futures == null || futures.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("No futures provided"));
        }

        CompletableFuture<T> result = new CompletableFuture<>();
        for (CompletableFuture<T> future : futures) {
            future.whenComplete((value, throwable) -> {
                if (throwable != null) {
                    result.completeExceptionally(throwable);
                } else {
                    result.complete(value);
                }
            });
        }
        return result;
    }

    /**
     * Get execution time
     */
    public Duration getExecutionTime(LocalDateTime startTime) {
        return Duration.between(startTime, LocalDateTime.now());
    }

    /**
     * Log execution time
     */
    public void logExecutionTime(String operation, LocalDateTime startTime) {
        Duration duration = getExecutionTime(startTime);
        log.info("Operation '{}' completed in {} ms", operation, duration.toMillis());
    }

    // ========== BATCH RESULT CLASS ==========

    /**
     * Result class for batch operations with error handling
     */
    public static class BatchResult<T> {
        private final List<T> results;
        private final List<Exception> errors;

        public BatchResult(List<T> results, List<Exception> errors) {
            this.results = results != null ? results : new ArrayList<>();
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public List<T> getResults() {
            return results;
        }

        public List<Exception> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean isSuccess() {
            return errors.isEmpty();
        }

        public int getSuccessCount() {
            return results.size();
        }

        public int getErrorCount() {
            return errors.size();
        }

        public int getTotalCount() {
            return results.size() + errors.size();
        }

        public double getSuccessRate() {
            int total = getTotalCount();
            return total > 0 ? (double) getSuccessCount() / total : 0.0;
        }

        @Override
        public String toString() {
            return String.format("BatchResult{success=%d, errors=%d, successRate=%.2f%%}",
                    getSuccessCount(), getErrorCount(), getSuccessRate() * 100);
        }
    }
}
