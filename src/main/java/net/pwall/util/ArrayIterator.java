/*
 * @(#) ArrayIterator.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014, 2017 Peter Wall
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} over the members of an array.  The iterator does not allow removals.
 *
 * @author  Peter Wall
 * @param   <E> the type of the array element
 */
public class ArrayIterator<E> implements Iterator<E> {

    private E[] array;
    private int index;

    /**
     * Construct an interator over the given array.
     *
     * @param   array   the array
     */
    public ArrayIterator(E[] array) {
        this.array = array;
        index = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return array[index++];
    }

}
