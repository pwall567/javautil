/*
 * @(#) OrderedSet.java
 */

package net.pwall.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * An implementation of the {@link Set} interface using an ordered list and binary search.
 *
 * @author  Peter Wall
 * @param   <E>     the element type
 */
public class OrderedSet<E> extends AbstractSet<E> {

    private List<E> list;
    private Comparator<E> comparator;

    /**
     * Construct an {@code OrderedSet}.
     *
     * @param   comparator  the comparator that determines the order of the set
     */
    public OrderedSet(Comparator<E> comparator) {
        list = new ArrayList<>();
        this.comparator = comparator;
    }

    /**
     * Construct an {@code OrderedSet}, providing an initial collection.
     *
     * @param   comparator  the comparator that determines the order of the set
     * @param   c           the initial contents
     */
    public OrderedSet(Comparator<E> comparator, Collection<? extends E> c) {
        this(comparator);
        addAll(c);
    }

    /**
     * Test whether the set contains the specified object.
     *
     * @param   o   the object
     * @return  {@code true} if the object is contained in the set
     * @throws  NullPointerException    if the object is {@code null}
     * @throws  ClassCastException      if the object is not of the class of the set
     * @see     Collection#contains(Object)
     */
    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        E c = (E)Objects.requireNonNull(o);
        int lo = 0;
        int hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >> 1;
            int comp = comparator.compare(list.get(mid), c);
            if (comp == 0)
                return true;
            if (comp < 0)
                lo = mid + 1;
            else
                hi = mid;
        }
        return false;
    }

    /**
     * Add an element to the set.
     *
     * @param   e   the new element
     * @return  {@code true} if the set changed as a result of the operation
     * @throws  NullPointerException if the object is {@code null}
     * @see     Collection#add(Object)
     */
    @Override
    public boolean add(E e) {
        Objects.requireNonNull(e);
        int lo = 0;
        int hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >> 1;
            int comp = comparator.compare(list.get(mid), e);
            if (comp == 0)
                return false;
            if (comp < 0)
                lo = mid + 1;
            else
                hi = mid;
        }
        list.add(lo, e);
        return true;
    }

    /**
     * Remove the specified object from the set.
     *
     * @param   o   the object to be removed
     * @throws  NullPointerException    if the object is {@code null}
     * @throws  ClassCastException      if the object is not of the class of the set
     * @see     Collection#remove(Object)
     */
    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        E c = (E)Objects.requireNonNull(o);
        int lo = 0;
        int hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >> 1;
            int comp = comparator.compare(list.get(mid), c);
            if (comp == 0) {
                list.remove(mid);
                return true;
            }
            if (comp < 0)
                lo = mid + 1;
            else
                hi = mid;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see Collection#clear()
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Returns an {@link Iterator} over the elements contained in this set.  The elements are
     * returned in the order determined by the {@link Comparator} associated with the set.
     *
     * @return  the {@link Iterator}
     * @see     Collection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return new Iter();
    }

    /**
     * {@inheritDoc}
     * @see Collection#size()
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Create a new {@code OrderedSet} of {@link Comparable} objects.
     *
     * @param   <C>     the class of the elements
     * @return          the new set
     */
    public static <C extends Comparable<C>> OrderedSet<C> create() {
        return new OrderedSet<>(new Comparator<C>() {
            @Override public int compare(C o1, C o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public class Iter implements Iterator<E> {

        private int index = 0;
        private boolean removeAllowed = false;

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            removeAllowed = true;
            return list.get(index++);
        }

        @Override
        public void remove() {
            if (!removeAllowed)
                throw new IllegalStateException();
            removeAllowed = false;
            list.remove(--index);
        }

    }

}
