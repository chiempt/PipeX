package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collection manipulation utilities với enterprise-grade features
 * - Null-safe operations
 * - Filter, map, reduce operations
 * - Set operations (intersection, union, difference)
 * - Partitioning và grouping
 */
@Slf4j
public final class CollectionUtils {

    private CollectionUtils() {
        // Utility class
    }

    // ========== NULL SAFETY ==========

    /**
     * Check if collection is null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Check if collection is not null and not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Return safe collection (empty if null)
     */
    public static <T> Collection<T> safe(Collection<T> collection) {
        return collection != null ? collection : Collections.emptyList();
    }

    /**
     * Return safe list (empty if null)
     */
    public static <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    /**
     * Return safe set (empty if null)
     */
    public static <T> Set<T> safeSet(Set<T> set) {
        return set != null ? set : Collections.emptySet();
    }

    /**
     * Return safe map (empty if null)
     */
    public static <K, V> Map<K, V> safeMap(Map<K, V> map) {
        return map != null ? map : Collections.emptyMap();
    }

    // ========== COLLECTION CREATION ==========

    /**
     * Create list from varargs
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        if (items == null)
            return Collections.emptyList();
        return Arrays.asList(items);
    }

    /**
     * Create set from varargs
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T... items) {
        if (items == null)
            return Collections.emptySet();
        return new HashSet<>(Arrays.asList(items));
    }

    /**
     * Create list from collection
     */
    public static <T> List<T> toList(Collection<T> collection) {
        return collection != null ? new ArrayList<>(collection) : Collections.emptyList();
    }

    /**
     * Create set from collection
     */
    public static <T> Set<T> toSet(Collection<T> collection) {
        return collection != null ? new HashSet<>(collection) : Collections.emptySet();
    }

    // ========== FILTERING OPERATIONS ==========

    /**
     * Filter collection with predicate
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Filter collection with predicate (null-safe)
     */
    public static <T> List<T> filterNullSafe(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .filter(Objects::nonNull)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Find first element matching predicate
     */
    public static <T> Optional<T> findFirst(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection))
            return Optional.empty();
        return collection.stream()
                .filter(predicate)
                .findFirst();
    }

    /**
     * Check if any element matches predicate
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection))
            return false;
        return collection.stream().anyMatch(predicate);
    }

    /**
     * Check if all elements match predicate
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection))
            return true;
        return collection.stream().allMatch(predicate);
    }

    // ========== MAPPING OPERATIONS ==========

    /**
     * Map collection to another type
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * Map collection to another type (null-safe)
     */
    public static <T, R> List<R> mapNullSafe(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Flat map collection
     */
    public static <T, R> List<R> flatMap(Collection<T> collection, Function<T, Stream<R>> mapper) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .flatMap(mapper)
                .collect(Collectors.toList());
    }

    // ========== REDUCTION OPERATIONS ==========

    /**
     * Reduce collection to single value
     */
    public static <T> Optional<T> reduce(Collection<T> collection, Function<T, Function<T, T>> reducer) {
        if (isEmpty(collection))
            return Optional.empty();
        return collection.stream().reduce((a, b) -> reducer.apply(a).apply(b));
    }

    /**
     * Sum collection of numbers
     */
    public static <T extends Number> double sum(Collection<T> collection) {
        if (isEmpty(collection))
            return 0.0;
        return collection.stream()
                .filter(Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .sum();
    }

    /**
     * Average of collection of numbers
     */
    public static <T extends Number> OptionalDouble average(Collection<T> collection) {
        if (isEmpty(collection))
            return OptionalDouble.empty();
        return collection.stream()
                .filter(Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .average();
    }

    // ========== SET OPERATIONS ==========

    /**
     * Intersection of two collections
     */
    public static <T> Set<T> intersection(Collection<T> collection1, Collection<T> collection2) {
        if (isEmpty(collection1) || isEmpty(collection2))
            return Collections.emptySet();

        Set<T> set1 = new HashSet<>(collection1);
        Set<T> set2 = new HashSet<>(collection2);
        set1.retainAll(set2);
        return set1;
    }

    /**
     * Union of two collections
     */
    public static <T> Set<T> union(Collection<T> collection1, Collection<T> collection2) {
        Set<T> result = new HashSet<>();
        if (isNotEmpty(collection1))
            result.addAll(collection1);
        if (isNotEmpty(collection2))
            result.addAll(collection2);
        return result;
    }

    /**
     * Difference of two collections (elements in first but not in second)
     */
    public static <T> Set<T> difference(Collection<T> collection1, Collection<T> collection2) {
        if (isEmpty(collection1))
            return Collections.emptySet();
        if (isEmpty(collection2))
            return new HashSet<>(collection1);

        Set<T> set1 = new HashSet<>(collection1);
        Set<T> set2 = new HashSet<>(collection2);
        set1.removeAll(set2);
        return set1;
    }

    // ========== PARTITIONING & GROUPING ==========

    /**
     * Partition collection into two lists based on predicate
     */
    public static <T> Map<Boolean, List<T>> partition(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            Map<Boolean, List<T>> result = new HashMap<>();
            result.put(true, Collections.emptyList());
            result.put(false, Collections.emptyList());
            return result;
        }

        return collection.stream()
                .collect(Collectors.partitioningBy(predicate));
    }

    /**
     * Group collection by key function
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> keyFunction) {
        if (isEmpty(collection))
            return Collections.emptyMap();
        return collection.stream()
                .collect(Collectors.groupingBy(keyFunction));
    }

    /**
     * Group collection by key function with value mapper
     */
    public static <T, K, V> Map<K, List<V>> groupBy(Collection<T> collection,
            Function<T, K> keyFunction,
            Function<T, V> valueFunction) {
        if (isEmpty(collection))
            return Collections.emptyMap();
        return collection.stream()
                .collect(Collectors.groupingBy(
                        keyFunction,
                        Collectors.mapping(valueFunction, Collectors.toList())));
    }

    // ========== SORTING ==========

    /**
     * Sort collection naturally
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> List<T> sort(Collection<T> collection) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Sort collection by key function
     */
    public static <T, K extends Comparable<K>> List<T> sortBy(Collection<T> collection, Function<T, K> keyFunction) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .sorted(Comparator.comparing(keyFunction))
                .collect(Collectors.toList());
    }

    /**
     * Sort collection by key function (descending)
     */
    public static <T, K extends Comparable<K>> List<T> sortByDesc(Collection<T> collection,
            Function<T, K> keyFunction) {
        if (isEmpty(collection))
            return Collections.emptyList();
        return collection.stream()
                .sorted(Comparator.comparing(keyFunction).reversed())
                .collect(Collectors.toList());
    }

    // ========== UTILITY OPERATIONS ==========

    /**
     * Get first element
     */
    public static <T> Optional<T> first(Collection<T> collection) {
        if (isEmpty(collection))
            return Optional.empty();
        return Optional.of(collection.iterator().next());
    }

    /**
     * Get last element
     */
    public static <T> Optional<T> last(List<T> list) {
        if (isEmpty(list))
            return Optional.empty();
        return Optional.of(list.get(list.size() - 1));
    }

    /**
     * Get element at index
     */
    public static <T> Optional<T> get(List<T> list, int index) {
        if (isEmpty(list) || index < 0 || index >= list.size())
            return Optional.empty();
        return Optional.of(list.get(index));
    }

    /**
     * Check if collection contains element
     */
    public static <T> boolean contains(Collection<T> collection, T element) {
        return isNotEmpty(collection) && collection.contains(element);
    }

    /**
     * Get collection size safely
     */
    public static int size(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }

    /**
     * Check if collections are equal (ignoring order)
     */
    public static <T> boolean equalsIgnoreOrder(Collection<T> collection1, Collection<T> collection2) {
        if (collection1 == collection2)
            return true;
        if (collection1 == null || collection2 == null)
            return false;
        if (collection1.size() != collection2.size())
            return false;

        return new HashSet<>(collection1).equals(new HashSet<>(collection2));
    }

    // ========== BATCH OPERATIONS ==========

    /**
     * Split collection into chunks
     */
    public static <T> List<List<T>> chunk(Collection<T> collection, int chunkSize) {
        if (isEmpty(collection) || chunkSize <= 0)
            return Collections.emptyList();

        List<T> list = toList(collection);
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }

        return chunks;
    }

    /**
     * Take first n elements
     */
    public static <T> List<T> take(Collection<T> collection, int n) {
        if (isEmpty(collection) || n <= 0)
            return Collections.emptyList();
        return collection.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Skip first n elements
     */
    public static <T> List<T> skip(Collection<T> collection, int n) {
        if (isEmpty(collection) || n <= 0)
            return toList(collection);
        return collection.stream()
                .skip(n)
                .collect(Collectors.toList());
    }
}
