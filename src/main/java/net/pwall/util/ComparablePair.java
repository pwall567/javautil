/*
 * @(#) ComparablePair.java
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
 * A comparable pair of objects.  Both of the objects must themselves be {@link Comparable}.
 *
 * @param   <F>   the class of the first object
 * @param   <S>   the class of the second object
 * @author  Peter Wall
 */
public class ComparablePair<F extends Comparable<? super F>, S extends Comparable<? super S>>
        extends Pair<F, S> implements Comparable<ComparablePair<F, S>> {

    private static final long serialVersionUID = -5981885794885300640L;

    /**
     * Construct a {@code ComparablePair} with the given values.
     *
     * @param   first   the value for the first object
     * @param   second  the value for the second object
     */
    public ComparablePair(F first, S second) {
        super(first, second);
    }

    /**
     * Compare this object with another for order.  The order is dependent on the order of the
     * first objects of each {@code ComparablePair}, or if they are equal, on the order of the
     * second objects.
     *
     * @param   o   the other object
     * @return  a negative value, zero, or a positive value, depending on whether this object is
     *          less than, equal to, or greater than the other object
     * @see     Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(ComparablePair<F, S> o) {
        if (o == null)
            return 1;
        int result = ComparableRef.compare(getFirst(), o.getFirst());
        return result != 0 ? result : ComparableRef.compare(getSecond(), o.getSecond());
    }

    /**
     * Create a {@code ComparablePair}, automatically deriving the classes of the objects.
     *
     * @param   <F>     Class of first object
     * @param   <S>     Class of second object
     * @param   first   the value for the first object
     * @param   second  the value for the second object
     * @return  the newly-constructed {@code ComparablePair}
     */
    public static <F extends Comparable<? super F>, S extends Comparable<? super S>>
            ComparablePair<F, S> create(F first, S second) {
        return new ComparablePair<F, S>(first, second);
    }

}
