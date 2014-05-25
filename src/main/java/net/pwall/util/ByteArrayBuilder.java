/*
 * @(#) ByteArrayBuilder.java
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

import java.util.Arrays;

/**
 * A dynamic byte array class.  This class allows byte array to be created dynamically, similar
 * to a {@link StringBuilder} for strings.
 * {@link java.io.ByteArrayOutputStream ByteArrayOutputStream} also provides equivalent
 * functionality, but its write methods are {@code synchronized}.
 *
 * @author Peter Wall
 */
public class ByteArrayBuilder {

    private byte[] buf;
    private int count;
    private int increment;

    public ByteArrayBuilder(int size, int increment) {
        if (size < 0)
            throw new IllegalArgumentException("Size must be >= 0");
        if (increment < 0)
            throw new IllegalArgumentException("Increment must be >= 0");
        buf = new byte[size];
        count = 0;
        this.increment = increment;
    }

    public ByteArrayBuilder(int size) {
        this(size, size / 2);
    }

    public ByteArrayBuilder() {
        this(20);
    }

    public ByteArrayBuilder(byte[] array) {
        this(array != null ? array.length : 20);
        append(array);
    }

    public int length() {
        return count;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    public byte[] toByteArray(int start) {
        if (start < 0 || start > count)
            throw new IndexOutOfBoundsException("start=" + start + "; count=" + count);
        return copyOf(start, count);
    }

    public byte[] toByteArray(int start, int end) {
        if (start < 0 || start > end || end > count)
            throw new IndexOutOfBoundsException("start=" + start + "; end=" + end +
                    "; count=" + count);
        return copyOf(start, end);
    }

    private byte[] copyOf(int start, int end) {
        int n = end - start;
        byte[] result = new byte[n];
        System.arraycopy(buf, start, result, 0, n);
        return result;
    }

    public void set(int index, int value) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        buf[index] = (byte)value;
    }

    private void ensureCapacity(int newCapacity) {
        int oldCapacity = buf.length;
        if (newCapacity > oldCapacity) {
            int proposed = oldCapacity + increment;
            if (proposed < newCapacity)
                proposed = newCapacity;
            buf = Arrays.copyOf(buf, proposed);
        }
    }

    public ByteArrayBuilder append(int b) {
        ensureCapacity(count + 1);
        buf[count++] = (byte)b;
        return this;
    }

    public ByteArrayBuilder append(byte[] array) {
        if (array != null) {
            int n = array.length;
            ensureCapacity(count + n);
            System.arraycopy(array, 0, buf, count, n);
            count += n;
        }
        return this;
    }

    public ByteArrayBuilder append(ByteArrayBuilder bab) {
        if (bab != null) {
            int n = bab.count;
            ensureCapacity(count + n);
            System.arraycopy(bab.buf, 0, buf, count, n);
            count += n;
        }
        return this;
    }

    public ByteArrayBuilder insert(int index, int b) {
        if (index < 0 || index > count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        ensureCapacity(count + 1);
        if (index < count)
            System.arraycopy(buf, index, buf, index + 1, count - index);
        buf[index] = (byte)b;
        count++;
        return this;
    }

    public ByteArrayBuilder insert(int index, byte[] array) {
        if (index < 0 || index > count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        if (array != null) {
            int n = array.length;
            ensureCapacity(count + n);
            if (index < count)
                System.arraycopy(buf, index, buf, index + n, count - index);
            System.arraycopy(array, 0, buf, index, n);
            count += n;
        }
        return this;
    }

    public ByteArrayBuilder insert(int index, ByteArrayBuilder bab) {
        if (index < 0 || index > count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        if (bab != null) {
            int n = bab.count;
            ensureCapacity(count + n);
            if (index < count)
                System.arraycopy(buf, index, buf, index + n, count - index);
            System.arraycopy(bab.buf, 0, buf, index, n);
            count += n;
        }
        return this;
    }

}
