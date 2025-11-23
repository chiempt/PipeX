package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Array manipulation utilities với enterprise-grade features
 * - Null-safe operations
 * - Type-safe array operations
 * - Convert between arrays and collections
 * - Array transformation utilities
 */
@Slf4j
public final class ArrayUtils {

    private ArrayUtils() {
        // Utility class
    }

    // ========== NULL SAFETY ==========

    /**
     * Check if array is null or empty
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if array is not null and not empty
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * Check if primitive array is null or empty
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if primitive array is not null and not empty
     */
    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    /**
     * Check if primitive array is null or empty
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if primitive array is not null and not empty
     */
    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    /**
     * Check if primitive array is null or empty
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if primitive array is not null and not empty
     */
    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    /**
     * Check if primitive array is null or empty
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if primitive array is not null and not empty
     */
    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }

    // ========== ARRAY CREATION ==========

    /**
     * Create array from varargs
     */
    @SafeVarargs
    public static <T> T[] arrayOf(T... items) {
        return items;
    }

    /**
     * Create array from collection
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        if (collection == null) {
            return (T[]) java.lang.reflect.Array.newInstance(componentType, 0);
        }
        return collection.toArray((T[]) java.lang.reflect.Array.newInstance(componentType, collection.size()));
    }

    /**
     * Create array from collection
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection) {
        if (collection == null)
            return (T[]) new Object[0];
        return (T[]) collection.toArray();
    }

    // ========== ARRAY OPERATIONS ==========

    /**
     * Get array length safely
     */
    public static int length(Object[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Get primitive array length safely
     */
    public static int length(int[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Get primitive array length safely
     */
    public static int length(long[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Get primitive array length safely
     */
    public static int length(double[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Get primitive array length safely
     */
    public static int length(boolean[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Get element at index safely
     */
    public static <T> Optional<T> get(T[] array, int index) {
        if (isEmpty(array) || index < 0 || index >= array.length) {
            return Optional.empty();
        }
        return Optional.of(array[index]);
    }

    /**
     * Get primitive element at index safely
     */
    public static OptionalInt get(int[] array, int index) {
        if (isEmpty(array) || index < 0 || index >= array.length) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(array[index]);
    }

    /**
     * Get primitive element at index safely
     */
    public static OptionalLong get(long[] array, int index) {
        if (isEmpty(array) || index < 0 || index >= array.length) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(array[index]);
    }

    /**
     * Get primitive element at index safely
     */
    public static OptionalDouble get(double[] array, int index) {
        if (isEmpty(array) || index < 0 || index >= array.length) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(array[index]);
    }

    /**
     * Get primitive element at index safely
     */
    public static Optional<Boolean> get(boolean[] array, int index) {
        if (isEmpty(array) || index < 0 || index >= array.length) {
            return Optional.empty();
        }
        return Optional.of(array[index]);
    }

    // ========== SEARCH OPERATIONS ==========

    /**
     * Find index of element
     */
    public static int indexOf(Object[] array, Object element) {
        if (isEmpty(array))
            return -1;
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find index of primitive element
     */
    public static int indexOf(int[] array, int element) {
        if (isEmpty(array))
            return -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find last index of element
     */
    public static int lastIndexOf(Object[] array, Object element) {
        if (isEmpty(array))
            return -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (Objects.equals(array[i], element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if array contains element
     */
    public static boolean contains(Object[] array, Object element) {
        return indexOf(array, element) >= 0;
    }

    /**
     * Check if primitive array contains element
     */
    public static boolean contains(int[] array, int element) {
        return indexOf(array, element) >= 0;
    }

    /**
     * Check if primitive array contains element
     */
    public static boolean contains(long[] array, long element) {
        if (isEmpty(array))
            return false;
        for (long value : array) {
            if (value == element)
                return true;
        }
        return false;
    }

    /**
     * Check if primitive array contains element
     */
    public static boolean contains(double[] array, double element) {
        if (isEmpty(array))
            return false;
        for (double value : array) {
            if (Double.compare(value, element) == 0)
                return true;
        }
        return false;
    }

    /**
     * Check if primitive array contains element
     */
    public static boolean contains(boolean[] array, boolean element) {
        if (isEmpty(array))
            return false;
        for (boolean value : array) {
            if (value == element)
                return true;
        }
        return false;
    }

    // ========== ARRAY TRANSFORMATION ==========

    /**
     * Reverse array
     */
    public static <T> T[] reverse(T[] array) {
        if (isEmpty(array))
            return array;

        T[] reversed = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Reverse primitive array
     */
    public static int[] reverse(int[] array) {
        if (isEmpty(array))
            return array;

        int[] reversed = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Reverse primitive array
     */
    public static long[] reverse(long[] array) {
        if (isEmpty(array))
            return array;

        long[] reversed = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Reverse primitive array
     */
    public static double[] reverse(double[] array) {
        if (isEmpty(array))
            return array;

        double[] reversed = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Reverse primitive array
     */
    public static boolean[] reverse(boolean[] array) {
        if (isEmpty(array))
            return array;

        boolean[] reversed = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    // ========== ARRAY FILTERING ==========

    /**
     * Filter array with predicate
     */
    public static <T> T[] filter(T[] array, Predicate<T> predicate) {
        if (isEmpty(array))
            return array;

        List<T> filtered = new ArrayList<>();
        for (T element : array) {
            if (predicate.test(element)) {
                filtered.add(element);
            }
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), filtered.size());
        return filtered.toArray(result);
    }

    /**
     * Filter primitive array with predicate
     */
    public static int[] filter(int[] array, Predicate<Integer> predicate) {
        if (isEmpty(array))
            return array;

        List<Integer> filtered = new ArrayList<>();
        for (int element : array) {
            if (predicate.test(element)) {
                filtered.add(element);
            }
        }

        return filtered.stream().mapToInt(Integer::intValue).toArray();
    }

    // ========== ARRAY MAPPING ==========

    /**
     * Map array to another type
     */
    public static <T, R> R[] map(T[] array, Function<T, R> mapper, Class<R> componentType) {
        if (isEmpty(array)) {
            @SuppressWarnings("unchecked")
            R[] result = (R[]) java.lang.reflect.Array.newInstance(componentType, 0);
            return result;
        }

        @SuppressWarnings("unchecked")
        R[] result = (R[]) java.lang.reflect.Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    /**
     * Map primitive array to another type
     */
    public static <R> R[] map(int[] array, Function<Integer, R> mapper, Class<R> componentType) {
        if (isEmpty(array)) {
            @SuppressWarnings("unchecked")
            R[] result = (R[]) java.lang.reflect.Array.newInstance(componentType, 0);
            return result;
        }

        @SuppressWarnings("unchecked")
        R[] result = (R[]) java.lang.reflect.Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    // ========== ARRAY REDUCTION ==========

    /**
     * Sum of array elements
     */
    public static int sum(int[] array) {
        if (isEmpty(array))
            return 0;
        return Arrays.stream(array).sum();
    }

    /**
     * Sum of array elements
     */
    public static long sum(long[] array) {
        if (isEmpty(array))
            return 0;
        return Arrays.stream(array).sum();
    }

    /**
     * Sum of array elements
     */
    public static double sum(double[] array) {
        if (isEmpty(array))
            return 0.0;
        return Arrays.stream(array).sum();
    }

    /**
     * Average of array elements
     */
    public static OptionalDouble average(int[] array) {
        if (isEmpty(array))
            return OptionalDouble.empty();
        return Arrays.stream(array).average();
    }

    /**
     * Average of array elements
     */
    public static OptionalDouble average(long[] array) {
        if (isEmpty(array))
            return OptionalDouble.empty();
        return Arrays.stream(array).average();
    }

    /**
     * Average of array elements
     */
    public static OptionalDouble average(double[] array) {
        if (isEmpty(array))
            return OptionalDouble.empty();
        return Arrays.stream(array).average();
    }

    /**
     * Maximum element in array
     */
    public static OptionalInt max(int[] array) {
        if (isEmpty(array))
            return OptionalInt.empty();
        return Arrays.stream(array).max();
    }

    /**
     * Maximum element in array
     */
    public static OptionalLong max(long[] array) {
        if (isEmpty(array))
            return OptionalLong.empty();
        return Arrays.stream(array).max();
    }

    /**
     * Maximum element in array
     */
    public static OptionalDouble max(double[] array) {
        if (isEmpty(array))
            return OptionalDouble.empty();
        return Arrays.stream(array).max();
    }

    /**
     * Minimum element in array
     */
    public static OptionalInt min(int[] array) {
        if (isEmpty(array))
            return OptionalInt.empty();
        return Arrays.stream(array).min();
    }

    /**
     * Minimum element in array
     */
    public static OptionalLong min(long[] array) {
        if (isEmpty(array))
            return OptionalLong.empty();
        return Arrays.stream(array).min();
    }

    /**
     * Minimum element in array
     */
    public static OptionalDouble min(double[] array) {
        if (isEmpty(array))
            return OptionalDouble.empty();
        return Arrays.stream(array).min();
    }

    // ========== ARRAY CONVERSION ==========

    /**
     * Convert array to list
     */
    public static <T> List<T> toList(T[] array) {
        return array != null ? Arrays.asList(array) : Collections.emptyList();
    }

    /**
     * Convert primitive array to list
     */
    public static List<Integer> toList(int[] array) {
        if (isEmpty(array))
            return Collections.emptyList();
        return IntStream.of(array).boxed().collect(Collectors.toList());
    }

    /**
     * Convert primitive array to list
     */
    public static List<Long> toList(long[] array) {
        if (isEmpty(array))
            return Collections.emptyList();
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    /**
     * Convert primitive array to list
     */
    public static List<Double> toList(double[] array) {
        if (isEmpty(array))
            return Collections.emptyList();
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    /**
     * Convert primitive array to list
     */
    public static List<Boolean> toList(boolean[] array) {
        if (isEmpty(array))
            return Collections.emptyList();
        List<Boolean> list = new ArrayList<>();
        for (boolean value : array) {
            list.add(value);
        }
        return list;
    }

    /**
     * Convert array to set
     */
    public static <T> Set<T> toSet(T[] array) {
        return array != null ? new HashSet<>(Arrays.asList(array)) : Collections.emptySet();
    }

    // ========== ARRAY UTILITIES ==========

    /**
     * Check if arrays are equal
     */
    public static boolean equals(Object[] array1, Object[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Check if primitive arrays are equal
     */
    public static boolean equals(int[] array1, int[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Check if primitive arrays are equal
     */
    public static boolean equals(long[] array1, long[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Check if primitive arrays are equal
     */
    public static boolean equals(double[] array1, double[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Check if primitive arrays are equal
     */
    public static boolean equals(boolean[] array1, boolean[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Get array hash code
     */
    public static int hashCode(Object[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Get primitive array hash code
     */
    public static int hashCode(int[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Get primitive array hash code
     */
    public static int hashCode(long[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Get primitive array hash code
     */
    public static int hashCode(double[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Get primitive array hash code
     */
    public static int hashCode(boolean[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Convert array to string
     */
    public static String toString(Object[] array) {
        return Arrays.toString(array);
    }

    /**
     * Convert primitive array to string
     */
    public static String toString(int[] array) {
        return Arrays.toString(array);
    }

    /**
     * Convert primitive array to string
     */
    public static String toString(long[] array) {
        return Arrays.toString(array);
    }

    /**
     * Convert primitive array to string
     */
    public static String toString(double[] array) {
        return Arrays.toString(array);
    }

    /**
     * Convert primitive array to string
     */
    public static String toString(boolean[] array) {
        return Arrays.toString(array);
    }
}
