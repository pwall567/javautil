/*
 * @(#) Ref.java
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
 * A reference to an object.
 *
 * @param <T> Class of the referred-to object
 * @author Peter Wall
 */
public class Ref<T> implements Serializable {

    private static final long serialVersionUID = 6434890119379911833L;

    private T object;

    /**
     * Construct a {@code Ref} with the given object.
     *
     * @param  object   the value for the object
     */
    public Ref(T object) {
        this.object = object;
    }

    /**
     * Get the object.
     *
     * @return the object
     */
    public T get() {
        return object;
    }

    /**
     * Get the object (alternative accessor to allow use of the reference in bean-style
     * accesses).
     *
     * @return the object
     */
    public T getObject() {
        return object;
    }

    /**
     * Return the {@link String} representation of the {@code Ref}.  The default representation
     * consists of the representation of the referred-to object enclosed in parentheses.
     *
     * @return the string representation
     * @see    Object#toString()
     */
    @Override
    public String toString() {
        return '(' + String.valueOf(get()) + ')';
    }

    /**
     * Return the hash code for the {@code Ref}.  The default hash code is the hash code of the
     * referred-to object.
     *
     * @return the hash code
     * @see    Object#hashCode()
     */
    @Override
    public int hashCode() {
        T object = get();  // in case getObject() has been overridden
        return (object == null ? 0 : object.hashCode());
    }

    /**
     * Compare this reference to another for equality.
     *
     * @param  other   the other reference
     * @return {@code true} if the other object is non-null, is a {@code Ref} to a compatible
     *         object, and that object is equal to this
     * @see    Object#equals(Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        return other instanceof Ref<?> && equal(get(), ((Ref<?>)other).get());
    }

    /**
     * Compare two objects for equality, allowing for nulls.  A null reference always compares
     * unequal to any non-null object, and two nulls are considered equal.  This method is
     * {@code public} and {@code static} so that it may be used as a comparison of two object
     * references even when the {@code Ref} class is not used.
     *
     * <p>Note - this method has been superseded since Java 1.7 by
     * {@code java.util.Objects.equals()}.</p>
     *
     * @param  a   the first object
     * @param  b   the second object
     * @return {@code true} if the first object is equal to the second object
     */
    public static boolean equal(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    /**
     * Create a new {@code Ref} where the type of the object is inferred from the type of the
     * argument.
     *
     * @param   <T>     class of the referred-to object
     * @param   object  the value for the object
     * @return  the new {@code Ref}
     */
    public static <T> Ref<T> create(T object) {
        return new Ref<>(object);
    }

}
