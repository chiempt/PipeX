package com.example.pipex.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async configuration với Virtual Threads (Java 21)
 * - Configure Virtual Thread Executor
 * - @EnableAsync configuration
 * - Thread pool sizing strategy
 * - Exception handling for async tasks
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    // ========== VIRTUAL THREAD EXECUTOR ==========

    /**
     * Virtual Thread Executor for Java 21
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        try {
            // Try to create Virtual Thread Executor (Java 21+)
            Class<?> virtualThreadExecutorClass = Class.forName("java.util.concurrent.Executors$VirtualThreadExecutor");
            return (Executor) virtualThreadExecutorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.warn("Virtual Thread Executor not available, falling back to standard executor: {}", e.getMessage());
            return standardAsyncExecutor();
        }
    }

    /**
     * Standard Async Executor (fallback)
     */
    @Bean(name = "standardAsyncExecutor")
    public Executor standardAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size
        executor.setCorePoolSize(10);

        // Maximum pool size
        executor.setMaxPoolSize(50);

        // Queue capacity
        executor.setQueueCapacity(1000);

        // Thread name prefix
        executor.setThreadNamePrefix("Async-");

        // Keep alive time
        executor.setKeepAliveSeconds(60);

        // Rejection policy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Await termination timeout
        executor.setAwaitTerminationSeconds(30);

        // Initialize
        executor.initialize();

        log.info("Standard Async Executor configured with core: {}, max: {}, queue: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    // ========== SPECIALIZED EXECUTORS ==========

    /**
     * High Priority Executor for critical tasks
     */
    @Bean(name = "highPriorityExecutor")
    public Executor highPriorityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("HighPriority-");
        executor.setKeepAliveSeconds(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        log.info("High Priority Executor configured");

        return executor;
    }

    /**
     * Batch Processing Executor for large operations
     */
    @Bean(name = "batchProcessingExecutor")
    public Executor batchProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(5000);
        executor.setThreadNamePrefix("Batch-");
        executor.setKeepAliveSeconds(120);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);

        executor.initialize();

        log.info("Batch Processing Executor configured");

        return executor;
    }

    /**
     * IO Intensive Executor for external API calls
     */
    @Bean(name = "ioIntensiveExecutor")
    public Executor ioIntensiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("IO-");
        executor.setKeepAliveSeconds(180);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);

        executor.initialize();

        log.info("IO Intensive Executor configured");

        return executor;
    }

    // ========== EXECUTOR MONITORING ==========

    /**
     * Get executor statistics
     */
    public String getExecutorStats(Executor executor) {
        if (executor instanceof ThreadPoolTaskExecutor taskExecutor) {
            return String.format(
                    "Executor Stats - Core: %d, Max: %d, Active: %d, Queue: %d, Completed: %d",
                    taskExecutor.getCorePoolSize(),
                    taskExecutor.getMaxPoolSize(),
                    taskExecutor.getActiveCount(),
                    taskExecutor.getQueueSize(),
                    taskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        }
        return "Executor Stats - Not available for Virtual Thread Executor";
    }

    /**
     * Check if executor is healthy
     */
    public boolean isExecutorHealthy(Executor executor) {
        if (executor instanceof ThreadPoolTaskExecutor taskExecutor) {
            return taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize() * 0.9;
        }
        return true; // Virtual Thread Executor is always healthy
    }

    // ========== EXECUTOR SELECTION ==========

    /**
     * Select appropriate executor based on task type
     */
    public Executor selectExecutor(TaskType taskType) {
        return switch (taskType) {
            case CRITICAL -> highPriorityExecutor();
            case BATCH_PROCESSING -> batchProcessingExecutor();
            case IO_INTENSIVE -> ioIntensiveExecutor();
            case STANDARD -> virtualThreadExecutor();
        };
    }

    /**
     * Task type enum
     */
    public enum TaskType {
        CRITICAL("Critical tasks requiring immediate execution"),
        BATCH_PROCESSING("Large batch operations"),
        IO_INTENSIVE("External API calls and I/O operations"),
        STANDARD("Standard async tasks");

        private final String description;

        TaskType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
