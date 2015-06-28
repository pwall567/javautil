/*
 * @(#) ListArray.java
 */

package net.pwall.util;

import java.util.AbstractList;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * Utility class to access an array as a {@link List}.
 */
public class ListArray<T> extends AbstractList<T> implements RandomAccess {

    public T[] array;

    public ListArray(T[] array) {
        this.array = Objects.requireNonNull(array);
    }

    @Override
    public T get(int index) {
        return array[index];
    }

    @Override
    public T set(int index, T element) {
        T result = array[index];
        array[index] = element;
        return result;
    }

    @Override
    public int size() {
        return array.length;
    }

    public static <E> ListArray<E> asList(E[] array) {
        return new ListArray<>(array);
    }

}
