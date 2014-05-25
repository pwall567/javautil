/*
 * @(#) ComparableRef.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014 Peter Wall
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

/**
 * A comparable reference to an object.  The object must itself be {@link Comparable}.
 *
 * @param  <T>  the class of the referred-to object
 * @author Peter Wall
 */
public class ComparableRef<T extends Comparable<? super T>> extends Ref<T>
        implements Comparable<ComparableRef<T>> {

    private static final long serialVersionUID = 6488478557809054277L;

    /**
     * Construct a {@code ComparableRef} with the given value.
     *
     * @param  object  the value for the object
     */
    public ComparableRef(T object) {
        super(object);
    }

    /**
     * Compare this reference with another for order.  The order is dependent on the order of
     * the referred-to objects of each {@code ComparableRef}.
     *
     * @param  o   the other reference
     * @return a negative value, zero, or a positive value, depending on whether this reference
     *         is less than, equal to, or greater than the other reference
     * @see    Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(ComparableRef<T> o) {
        if (o == null)
            return 1;
        return compare(get(), o.get());
    }

    /**
     * Compare two objects for order, allowing for nulls.  A null reference always compares
     * lower than any non-null object, and two nulls are considered equal.  This method is
     * {@code public} and {@code static} so that it may be used as a comparison of two object
     * references even when the {@code ComparableRef} class is not used.
     *
     * @param  a   the first object
     * @param  b   the second object
     * @return a negative value, zero, or a positive value, depending on whether the first
     *         object is less than, equal to, or greater than the second object
     */
    public static <T extends Comparable<? super T>> int compare(T a, T b) {
        if (a == b)
            return 0;
        if (a == null)
            return -1; // b must be non-null because of previous test
        return b == null ? 1 : a.compareTo(b);
    }

}
