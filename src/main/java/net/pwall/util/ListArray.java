/*
 * @(#) ListArray.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014, 2015, 2016, 2017 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.util;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * Utility class to access an array as a {@link List}.  This implementation does not support the
 * {@link List#remove(int)}, {@link List#remove(Object)},
 * {@link List#removeAll(java.util.Collection)} or {@link List#retainAll(java.util.Collection)}
 * operations, so the default implementations will apply, throwing an
 * {@link UnsupportedOperationException}.
 */
public class ListArray<T> extends AbstractList<T> implements RandomAccess {

    public T[] array;

    /**
     * Construct a {@link ListArray} with the given array.
     *
     * @param   array   the array
     * @throws  NullPointerException if the array is {@code null}
     */
    public ListArray(T[] array) {
        this.array = Objects.requireNonNull(array);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(int index) {
        return array[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T set(int index, T element) {
        T result = array[index];
        array[index] = element;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return array.length;
    }

    /**
     * Convenience method to construct a {@code ListArray} from an array.
     *
     * @param   array   the array
     * @return  the {@code ListArray}
     * @throws  NullPointerException if the array is {@code null}
     */
    public static <E> ListArray<E> asList(E[] array) {
        return new ListArray<>(array);
    }

}
