/*
 * @(#) CharIterator.java
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over the characters of a {@link CharSequence} (a {@link String},
 * {@link StringBuilder} etc.).
 *
 * @author Peter Wall
 */
public class CharIterator implements Iterator<Character> {

    private CharSequence seq;
    private int index;
    private int limit;

    public CharIterator(CharSequence seq, int start, int end) {
        if (seq == null)
            throw new NullPointerException();
        if (start < 0 || end > seq.length() || start > end)
            throw new IndexOutOfBoundsException();
        this.seq = seq;
        index = start;
        limit = end;
    }

    public CharIterator(CharSequence seq) {
        this(seq, 0, seq.length());
    }

    @Override
    public boolean hasNext() {
        return index < limit;
    }

    @Override
    public Character next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return Character.valueOf(seq.charAt(index++));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
