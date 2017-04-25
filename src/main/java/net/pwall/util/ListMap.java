/*
 * @(#) ListMap.java
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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * An implementation of {@link Map} that uses a {@link List} to hold the entries, retaining the
 * order of insertion.  This class is useful when the order of insertion of entries is
 * significant, but it suffers from serious performance degradation when the numbers of entries
 * exceeds a handful.
 *
 * @author  Peter Wall
 * @param   <K>         the key type
 * @param   <V>         the value type
 */
public class ListMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = -5594713182082941289L;

    protected List<ListMap.Entry<K, V>> list;

    /**
     * Construct an empty {@code ListMap}.
     */
    public ListMap() {
        list = new ArrayList<>();
    }

    /**
     * Construct an empty {@code ListMap} with a specified initial capacity.
     *
     * @param   capacity    the initial capacity
     */
    public ListMap(int capacity) {
        list = new ArrayList<>(capacity);
    }

    /**
     * Construct a {@code ListMap} with the contents of another {@link Map}.
     *
     * @param   m   the other {@link Map}
     */
    public ListMap(Map<? extends K, ? extends V> m) {
        this(m.size());
        putAll(m);
    }

    /**
     * Get a value from the {@code ListMap}.
     *
     * @param   key     the key of the value
     * @return          the value, or {@code null} if not found
     * @see     Map#get(Object)
     */
    @Override
    public V get(Object key) {
        int index = findIndex(Objects.requireNonNull(key));
        return index < 0 ? null : list.get(index).getValue();
    }

    /**
     * Test whether the {@code ListMap} contains a specified key.
     *
     * @param   key     the key to test for
     * @return          {@code true} if the key is found
     * @see     Map#containsKey(Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return findIndex(Objects.requireNonNull(key)) >= 0;
    }

    /**
     * Store a value in the {@code ListMap} with the specified key.
     *
     * @param   key     the key
     * @param   value   the value
     * @return          the previous value stored with that key, or {@code null} if no previous
     *                  value
     * @see     Map#put(Object, Object)
     */
    @Override
    public V put(K key, V value) {
        int index = findIndex(Objects.requireNonNull(key));
        if (index >= 0) {
            Entry<K, V> entry = list.get(index);
            V oldValue = entry.getValue();
            entry.setValue(value);
            return oldValue;
        }
        list.add(new Entry<>(key, value));
        return null;
    }

    /**
     * Remove the specified key-value mapping from the {@code ListMap}.
     *
     * @param   key     the key
     * @return          the value stored with that key, or {@code null} if key not used
     * @see     Map#remove(Object)
     */
    @Override
    public V remove(Object key) {
        int index = findIndex(Objects.requireNonNull(key));
        if (index >= 0)
            return list.remove(index).getValue();
        return null;
    }

    /**
     * Get the number of values in the {@code ListMap}.
     *
     * @return  the number of values
     * @see     Map#size()
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Test whether the {@code ListMap}is empty.
     *
     * @return  {@code true} if the {@code ListMap} is empty
     * @see     Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Test whether the {@code ListMap} contains the specified value.
     *
     * @param   value   the value
     * @return          {@code true} if the {@code ListMap} contains the value
     * @see     Map#containsValue(Object)
     */
    @Override
    public boolean containsValue(Object value) {
        for (int i = 0, n = list.size(); i < n; i++)
            if (Objects.equals(list.get(i).getValue(), value))
                return true;
        return false;
    }

    /**
     * Add all the members of another {@link Map} to this {@code ListMap}.
     *
     * @param   m       the other {@link Map}
     * @see     Map#putAll(Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K k : m.keySet())
            put(k, m.get(k));
    }

    /**
     * Remove all members from this {@code ListMap}.
     *
     * @see     Map#clear()
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Get a {@link Set} representing the keys in use in the {@code ListMap}.
     *
     * @return  the {@link Set} of keys
     * @see     Map#keySet()
     */
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    /**
     * Get a {@link Collection} of the values in the {@code ListMap}.
     *
     * @return  the {@link Collection} of values
     * @see     Map#values()
     */
    @Override
    public Collection<V> values() {
        return new ValueCollection();
    }

    /**
     * Get a {@link Set} of the key-value pairs in use in the {@code ListMap}.
     *
     * @return  the {@link Set} of key-value pairs
     * @see     Map#entrySet()
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Get the hash code for this {@code ListMap}.
     *
     * @return  the hash code
     * @see     Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0, n = list.size(); i < n; i++)
            result ^= list.get(i).hashCode();
        return result;
    }

    /**
     * Compare this {@code ListMap} with another object for equality.
     *
     * @param   other   the other object
     * @return  {@code true} if the other object is a {@code ListMap} and is identical to
     *          this object
     * @see     Object#equals(Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof ListMap<?, ?>))
            return false;
        ListMap<?, ?> otherMapping = (ListMap<?, ?>)other;
        if (list.size() != otherMapping.list.size())
            return false;
        for (Entry<K, V> entry : list)
            if (!Objects.equals(entry.getValue(), otherMapping.get(entry.getKey())))
                return false;
        return true;
    }

    /**
     * Get an {@link Entry} by index.
     *
     * @param   index   the index
     * @return  the list entry
     */
    public Entry<K, V> getEntry(int index) {
        return list.get(index);
    }

    /**
     * Find the index for the specified key.
     *
     * @param   key     the key
     * @return          the index for this key, or -1 if not found
     */
    protected int findIndex(Object key) {
        for (int i = 0, n = list.size(); i < n; i++)
            if (list.get(i).getKey().equals(key))
                return i;
        return -1;
    }

    /**
     * Inner class to represent a key-value pair in the {@code ListMap}.
     *
     * @param   <KK>    the key type
     * @param   <VV>    the value type
     */
    public static class Entry<KK, VV> implements Map.Entry<KK, VV>, Serializable {

        private static final long serialVersionUID = -7610378954393786210L;

        private KK key;
        private VV value;

        /**
         * Construct an {@code Entry} with the given key and value.
         *
         * @param   key     the key
         * @param   value   the value
         */
        public Entry(KK key, VV value) {
            this.key = key;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public KK getKey() {
            return key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public VV getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public VV setValue(VV value) {
            VV oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (!(other instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> otherEntry = (Map.Entry<?, ?>)other;
            return Objects.equals(key, otherEntry.getKey()) &&
                    Objects.equals(value, otherEntry.getValue());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

    }

    /**
     * An {@link Iterator} over the {@link Set} of key-value pairs in the {@code ListMap}.
     */
    private class EntryIterator extends BaseIterator<Map.Entry<K, V>> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Entry<K, V> next() {
            return nextEntry();
        }

    }

    /**
     * An {@link Iterator} over the {@link Set} of keys in the {@code ListMap}.
     */
    private class KeyIterator extends BaseIterator<K> {

        /**
         * {@inheritDoc}
         */
        @Override
        public K next() {
            return nextEntry().getKey();
        }

    }

    /**
     * An {@link Iterator} over the {@link Collection} of values in the {@code ListMap}.
     */
    private class ValueIterator extends BaseIterator<V> {

        /**
         * {@inheritDoc}
         */
        @Override
        public V next() {
            return nextEntry().getValue();
        }

    }

    /**
     * Abstract base class for various iterators.
     *
     * @param   <T>     the returned type
     */
    private abstract class BaseIterator<T> implements Iterator<T> {

        private int index;

        /**
         * Construct a {@code BaseIterator} starting at index 0.
         */
        public BaseIterator() {
            index = 0;
        }

        /**
         * Get the next {@code Entry} from the list.  The different derived iterator types will
         * return the entry itself, or the key or value.
         *
         * @return  the next {@code Entry}
         */
        public Entry<K, V> nextEntry() {
            if (!hasNext())
                throw new NoSuchElementException();
            return list.get(index++);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return index < list.size();
        }

    }

    /**
     * A collection of the key-value pairs in the {@code ListMap}.
     *
     * @see     #entrySet()
     */
    private class EntrySet extends CollectionBase<Map.Entry<K, V>> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c)
                if (!list.contains(o))
                    return false;
            return true;
        }

    }

    /**
     * A collection of the keys in the {@code ListMap}.
     *
     * @see     #keySet()
     */
    private class KeySet extends CollectionBase<K> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c)
                if (!containsKey(o))
                    return false;
            return true;
        }

    }

    /**
     * A collection of the values in the {@code ListMap}.
     *
     * @see     #values()
     */
    private class ValueCollection extends CollectionBase<V> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c)
                if (!containsValue(o))
                    return false;
            return true;
        }

    }

    /**
     * Abstract base class for various returned collections.  All modifying operations throw an
     * {@link UnsupportedOperationException}.
     *
     * @param   <T>     the returned type
     */
    private abstract class CollectionBase<T> extends AbstractSet<T> {

        /**
         * Return the number of elements in the set.  All returned collections are the same size
         * as the underlying collection.
         *
         * @return  the number of elements in the collection
         */
        @Override
        public int size() {
            return list.size();
        }

        /**
         * Remove an object from the collection - not supported.
         *
         * @param   o   the object
         * @return      (never returns normally)
         * @throws      UnsupportedOperationException in all cases
         */
        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        /**
         * Add all elements from another collection - not supported.
         *
         * @param   c   the other collection
         * @return      (never returns normally)
         * @throws      UnsupportedOperationException in all cases
         */
        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        /**
         * Remove all elements not matching those in another collection - not supported.
         *
         * @param   c   the other collection
         * @return      (never returns normally)
         * @throws      UnsupportedOperationException in all cases
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        /**
         * Remove all elements matching those in another collection - not supported.
         *
         * @param   c   the other collection
         * @return      (never returns normally)
         * @throws      UnsupportedOperationException in all cases
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        /**
         * Clear the collection - not supported.
         *
         * @throws      UnsupportedOperationException in all cases
         */
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

    }

}
