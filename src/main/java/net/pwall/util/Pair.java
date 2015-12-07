/*
 * @(#) Pair.java
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

import java.io.Serializable;

/**
 * A pair of objects.
 *
 * @param <F> Class of first object
 * @param <S> Class of second object
 * @author Peter Wall
 */
public class Pair<F, S> implements Serializable {

    private static final long serialVersionUID = 3881544420119098149L;

    private F first;
    private S second;

    /**
     * Construct a {@code Pair} with the given values.
     *
     * @param  first   the value for the first object
     * @param  second  the value for the second object
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get the first object.
     *
     * @return the first object
     */
    public F getFirst() {
        return first;
    }

    /**
     * Get the second object.
     *
     * @return the second object
     */
    public S getSecond() {
        return second;
    }

    /**
     * Return the {@link String} representation of the {@code Pair}.  The default representation
     * consists of the representations of the two objects, enclosed in parentheses and separated
     * by a comma.
     *
     * @return the string representation
     * @see    Object#toString()
     */
    @Override
    public String toString() {
        return '(' + String.valueOf(getFirst()) + ',' + String.valueOf(getSecond()) + ')';
    }

    /**
     * Return the hash code for the {@code Pair}.  The default hash code is the exclusive OR of
     * the hash codes of the two objects.
     *
     * @return the hash code
     * @see    Object#hashCode()
     */
    @Override
    public int hashCode() {
        F f = getFirst();  // in case getFirst() has been overridden
        S s = getSecond(); // in case getSecond() has been overridden
        return (f == null ? 0 : f.hashCode()) ^ (s == null ? 0 : s.hashCode());
    }

    /**
     * Compare this object to another for equality.
     *
     * @param  other   the other object
     * @return {@code true} if the other object is non-null, is a {@code Pair} of compatible
     *         objects, and those objects are equal
     * @see    Object#equals(Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Pair<?, ?>))
            return false;
        Pair<?, ?> p = (Pair<?, ?>)other;
        return Ref.equal(getFirst(), p.getFirst()) && Ref.equal(getSecond(), p.getSecond());
    }

    /**
     * Create a new {@code Pair} where the types of the two objects are inferred from the types
     * of the arguments.
     *
     * @param   <F>     Class of first object
     * @param   <S>     Class of second object
     * @param   first   the value for the first object
     * @param   second  the value for the second object
     * @return  the new {@code Pair}
     */
    public static <F, S> Pair<F, S> create(F first, S second) {
        return new Pair<>(first, second);
    }

}
