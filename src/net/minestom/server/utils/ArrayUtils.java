package net.minestom.server.utils;

import java.util.function.Supplier;

import java.util.ArrayList;
import net.minestom.server.utils.validate.Check;

public final class ArrayUtils {

    private ArrayUtils() {

    }

    public static int[] concatenateIntArrays(int[]... arrays) {
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }
        int[] result = new int[totalLength];

        int startingPos = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, startingPos, array.length);
            startingPos += array.length;
        }

        return result;
    }

    public static void removeElement(Object[] arr, int index) {
        System.arraycopy(arr, index + 1, arr, index, arr.length - 1 - index);
    }

    public static void copyToDestination(short[] src, short[] dest) {
        Check.argCondition(src.length != dest.length, "The two arrays need to have the same length.");
        System.arraycopy(src, 0, dest, 0, src.length);
    }

    /**
     * Gets the differences between 2 arrays.
     *
     * @param a the first array
     * @param b the second array
     * @return an array containing a's indexes that aren't in b array
     */
    public static int[] getDifferencesBetweenArray(long[] a, long[] b) {
        int counter = 0;
        int[] indexes = new int[Math.max(a.length, b.length)];

        for (int i = 0; i < a.length; i++) {
            final long aValue = a[i];
            boolean contains = false;
            for (final long bValue : b) {
                if (bValue == aValue) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                indexes[counter++] = i;
            }
        }

        // Resize array
        int[] result = new int[counter];
        System.arraycopy(indexes, 0, result, 0, counter);
        return result;
    }

    public static int[] toArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Gets if two arrays share the same start until {@code length}.
     *
     * @param array1 the first array
     * @param array2 the second array
     * @param length the length to check (0-length)
     * @param <T>    the type of the arrays
     * @return true if both arrays share the same start
     */
    public static <T> boolean sameStart(T[] array1, T[] array2, int length) {
        if (length > array1.length || length > array2.length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            final T value1 = array1[i];
            for (int j = 0; j < length; j++) {
                final T value2 = array2[j];
                if (!value1.equals(value2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fills an array using a supplier.
     *
     * @param array    the array to fill
     * @param supplier the supplier to fill the array
     * @param <T>      the array type
     */
    public static <T> void fill(T[] array, Supplier<T> supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
        }
    }

}
