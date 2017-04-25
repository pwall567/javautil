/*
 * @(#) ByteArrayBuilder.java
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

import java.io.IOException;
import java.io.InputStream;
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

    public static final int MINIMUM_READ_BUFFER_SIZE = 4096;

    private byte[] buf;
    private int count;
    private int increment;

    /**
     * Construct a {@code ByteArrayBuilder} with the specified initial size and increment.
     *
     * @param   size        the initial size (may be zero)
     * @param   increment   the amount to increment the size by when necessary
     * @throws  IllegalArgumentException if the size is negative or the increment is not
     *          positive
     */
    public ByteArrayBuilder(int size, int increment) {
        if (size < 0)
            throw new IllegalArgumentException("Size must be >= 0");
        if (increment <= 0)
            throw new IllegalArgumentException("Increment must be > 0");
        buf = new byte[size];
        count = 0;
        this.increment = increment;
    }

    /**
     * Construct a {@code ByteArrayBuilder} with the specified initial size.
     *
     * @param   size        the initial size
     * @throws  IllegalArgumentException if the size is negative
     */
    public ByteArrayBuilder(int size) {
        this(size, Math.max(size / 2, 1));
    }

    /**
     * Construct a {@code ByteArrayBuilder} with the default initial size of 20.
     */
    public ByteArrayBuilder() {
        this(20);
    }

    /**
     * Construct a {@code ByteArrayBuilder} from an existing byte array.
     *
     * @param   array   the byte array
     */
    public ByteArrayBuilder(byte[] array) {
        this(array != null ? array.length : 20);
        append(array);
    }

    /**
     * Construct a {@code ByteArrayBuilder} from another {@code ByteArrayBuilder}.
     */
    public ByteArrayBuilder(ByteArrayBuilder bab) {
        this(bab != null ? bab.length() : 20);
        append(bab);
    }

    /**
     * Get the length of the {@code ByteArrayBuilder} (the number of bytes used).
     *
     * @return  the current length
     */
    public int length() {
        return count;
    }

    /**
     * Get a byte array of only the bytes used.
     *
     * @return  the byte array
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Get a byte array of only the bytes used, starting from a nominated point.
     *
     * @param   start   the start index
     * @return  the byte array
     * @throws  IndexOutOfBoundsException if the start index is outside the bounds of the array
     */
    public byte[] toByteArray(int start) {
        if (start < 0 || start > count)
            throw new IndexOutOfBoundsException("start=" + start + "; count=" + count);
        return copyOf(start, count);
    }

    /**
     * Get a byte array of only the bytes used, using a nominated start and end point.
     *
     * @param   start   the start index
     * @param   end     the end index
     * @return  the byte array
     * @throws  IndexOutOfBoundsException if the start or end index is not valid
     */
    public byte[] toByteArray(int start, int end) {
        if (start < 0 || start > end || end > count)
            throw new IndexOutOfBoundsException("start=" + start + "; end=" + end +
                    "; count=" + count);
        return copyOf(start, end);
    }

    /**
     * Make a copy of a specified part of the array, assuming range checks already done.
     *
     * @param   start   the start index
     * @param   end     the end index
     * @return  the byte array
     */
    private byte[] copyOf(int start, int end) {
        int n = end - start;
        byte[] result = new byte[n];
        System.arraycopy(buf, start, result, 0, n);
        return result;
    }

    /**
     * Set a nominated byte in the array to a given value.
     *
     * @param   index   the index to be modified
     * @param   value   the new value
     * @return  the previous value at that index
     * @throws  IndexOutOfBoundsException if the index is not valid
     */
    public byte set(int index, int value) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        byte result = buf[index];
        buf[index] = (byte)value;
        return result;
    }

    /**
     * Get a nominated byte from the array.
     *
     * @param   index   the index of the byte
     * @return  the value at that index
     * @throws  IndexOutOfBoundsException if the index is not valid
     */
    public byte get(int index) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("index=" + index + "; count=" + count);
        return buf[index];
    }

    /**
     * Ensure the array has sufficient capacity for new data.
     *
     * @param   newCapacity     the capacity required
     */
    private void ensureCapacity(int newCapacity) {
        int oldCapacity = buf.length;
        if (newCapacity > oldCapacity) {
            int proposed = oldCapacity + increment;
            if (proposed < newCapacity)
                proposed = newCapacity;
            buf = Arrays.copyOf(buf, proposed);
        }
    }

    /**
     * Append an {@code int} as a single byte.
     *
     * @param   b       the new value
     * @return  {@code this} (for chaining)
     */
    public ByteArrayBuilder append(int b) {
        ensureCapacity(count + 1);
        buf[count++] = (byte)b;
        return this;
    }

    /**
     * Append a byte array.
     *
     * @param   array   the byte array
     * @return  {@code this} (for chaining)
     */
    public ByteArrayBuilder append(byte[] array) {
        if (array != null) {
            int n = array.length;
            ensureCapacity(count + n);
            System.arraycopy(array, 0, buf, count, n);
            count += n;
        }
        return this;
    }

    /**
     * Append a specified range of bytes from a byte array.
     *
     * @param   array   the byte array
     * @param   start   the start index
     * @param   end     the end index
     * @return  {@code this} (for chaining)
     */
    public ByteArrayBuilder append(byte[] array, int start, int end) {
        if (array != null) {
            if (start < 0 || start > end || end > array.length)
                throw new IndexOutOfBoundsException("start=" + start + "; end=" + end +
                        "; length=" + array.length);
            int n = end - start;
            ensureCapacity(count + n);
            System.arraycopy(array, start, buf, count, n);
            count += n;
        }
        return this;
    }

    /**
     * Append another {@code ByteArrayBuilder}.
     *
     * @param   bab     the other {@code ByteArrayBuilder}
     * @return  {@code this} (for chaining)
     */
    public ByteArrayBuilder append(ByteArrayBuilder bab) {
        if (bab != null) {
            int n = bab.length();
            ensureCapacity(count + n);
            System.arraycopy(bab.buf, 0, buf, count, n);
            count += n;
        }
        return this;
    }

    /**
     * Append the contents of an {@link InputStream}.
     *
     * @param   is      the {@link InputStream}
     * @return  {@code this} (for chaining)
     * @throws  IOException if thrown by the {@link InputStream}
     */
    public ByteArrayBuilder append(InputStream is) throws IOException {
        if (is != null) {
            int readSize = Math.max(buf.length, MINIMUM_READ_BUFFER_SIZE);
            byte[] readBuf = new byte[readSize];
            for (;;) {
                int i = is.read(readBuf);
                if (i < 0)
                    break;
                append(readBuf, 0, i);
            }
        }
        return this;
    }

    /**
     * Insert an {@code int} as a single byte at the nominated point in the array.  All
     * following bytes will be moved up make room.
     *
     * @param   index   the index at which to insert
     * @param   b       the new value
     * @return  {@code this} (for chaining)
     * @throws  IndexOutOfBoundsException if the index is not valid
     */
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

    /**
     * Insert a byte array at the nominated point in the array.  All following bytes will be
     * moved up make room.
     *
     * @param   index   the index at which to insert
     * @param   array   the byte array
     * @return  {@code this} (for chaining)
     * @throws  IndexOutOfBoundsException if the index is not valid
     */
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

    /**
     * Insert another {@code ByteArrayBuilder} at the nominated point in the array.  All
     * following bytes will be moved up make room.
     *
     * @param   index   the index at which to insert
     * @param   bab     the other {@code ByteArrayBuilder}
     * @return  {@code this} (for chaining)
     * @throws  IndexOutOfBoundsException if the index is not valid
     */
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

    /**
     * Convenience method to create a {@code ByteArrayBuilder}.  Supports the idiom:
     * <pre>
     *     ByteArrayBuilder bab = ByteArrayBuilder.create().append(0).append(1).append(2);
     * </pre>
     *
     * @return  the new {@code ByteArrayBuilder}
     */
    public ByteArrayBuilder create() {
        return new ByteArrayBuilder();
    }

    /**
     * Convenience method to create a {@code ByteArrayBuilder}, specifying the initial size.
     * Supports the idiom (for example):
     * <pre>
     *     InputStream is = new FileInputStream("data.bin");
     *     byte[] fileContents = ByteArrayBuilder.create(1000).append(is).toByteArray();
     * </pre>
     *
     * @param   size    the initial size
     * @return  the new {@code ByteArrayBuilder}
     * @throws  IllegalArgumentException if the size is negative
     */
    public ByteArrayBuilder create(int size) {
        return new ByteArrayBuilder(size);
    }

}
