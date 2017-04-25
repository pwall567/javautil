/*
 * @(#) SortedListMap.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2017 Peter Wall
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An implementation of {@link Map} that uses a {@link List} to hold the entries in sorted
 * order.  The keys are required to implement {@link Comparable}.
 *
 * @author  Peter Wall
 * @param   <K>         the key type
 * @param   <V>         the value type
 */
public class SortedListMap<K extends Comparable<K>, V> extends ListMap<K, V> {

    private static final long serialVersionUID = 8531216371555239765L;

    /**
     * Construct an empty {@code SortedListMap}.
     */
    public SortedListMap() {
        super();
    }

    /**
     * Construct an empty {@code SortedListMap} with a specified initial capacity.
     *
     * @param   capacity    the initial capacity
     */
    public SortedListMap(int capacity) {
        super(capacity);
    }

    /**
     * Construct a {@code SortedListMap} with the contents of another {@link Map}.
     *
     * @param   m   the other {@link Map}
     */
    public SortedListMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        int lo = 0;
        int hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >> 1;
            Entry<K, V> entry = list.get(mid);
            int comp = entry.getKey().compareTo(key);
            if (comp == 0) {
                V result = entry.getValue();
                entry.setValue(value);
                return result;
            }
            if (comp < 0)
                lo = mid + 1;
            else
                hi = mid;
        }
        list.add(lo, new Entry<>(key, value));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int findIndex(Object key) {
        @SuppressWarnings("unchecked")
        K keyObject = (K)key;
        int lo = 0;
        int hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >> 1;
            int comp = list.get(mid).getKey().compareTo(keyObject);
            if (comp == 0)
                return mid;
            if (comp < 0)
                lo = mid + 1;
            else
                hi = mid;
        }
        return -1;
    }

}
