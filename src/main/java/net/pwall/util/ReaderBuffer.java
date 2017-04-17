/*
 * @(#) ReaderBuffer.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2017 Peter Wall
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
import java.io.Reader;
import java.util.ArrayList;

/**
 * An implementation of {@link CharSequence} that provides read-only random access to the data
 * input from a {@link Reader}.
 *
 * @author Peter Wall
 */
public class ReaderBuffer implements CharSequence {

    /** Default buffer size = 4096 */
    public static final int defaultBufferSize = 4096;

    private int bufferSize;
    private char[][] buffers;
    private int length;
    private String str;

    /**
     * Create a {@code ReaderBuffer} with the contents taken from the nominated {@link Reader},
     * using the specified buffer size.  Read the data into a set of character arrays in an
     * {@link ArrayList}, so that it can be accessed by the {@link CharSequence} interface.
     *
     * @param   rdr         the {@link Reader}
     * @param   bufferSize  the buffer size to use
     * @throws  IOException if thrown by the {@link Reader}
     */
    public ReaderBuffer(Reader rdr, int bufferSize) throws IOException {
        if (bufferSize <= 0)
            throw new IllegalArgumentException("Buffer size must be positive");

        // initialise fields

        this.bufferSize = bufferSize;
        str = null;
        length = 0;
        ArrayList<char[]> bufferList = new ArrayList<>();

        // outer loop - read one buffer at a time

        for (;;) {

            // inner loop - read from Reader until buffer is filled

            char[] buffer = new char[bufferSize];
            int len = bufferSize;
            int offset = 0;
            do {
                int n = rdr.read(buffer, offset, len);
                if (n < 0) // EOF
                    break;
                offset += n;
                len -= n;
            } while (len > 0);
            if (offset == 0) // no data was read in inner loop
                break;

            // add buffer to list

            bufferList.add(buffer);
            length += bufferSize - len;
            if (len > 0) // inner loop terminated early - EOF
                break;
        }

        // get buffer list as array

        buffers = bufferList.toArray(new char[bufferList.size()][]);
    }

    /**
     * Create a {@code ReaderBuffer} with the contents taken from the nominated {@link Reader},
     * using the default buffer size.
     *
     * @param   rdr         the {@link Reader}
     * @throws  IOException if thrown by the {@link Reader}
     */
    public ReaderBuffer(Reader rdr) throws IOException {
        this(rdr, defaultBufferSize);
    }

    /**
     * Get the total length of the {@link CharSequence}.
     *
     * @return  the length
     * @see     CharSequence#length()
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * Get the character at the specified index.
     *
     * @param   index   the index
     * @return  the character
     * @throws  IndexOutOfBoundsException if index less than 0 or greater than length
     * @see     CharSequence#charAt(int)
     */
    @Override
    public char charAt(int index) {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException(String.valueOf(index));
        return buffers[index / bufferSize][index % bufferSize];
    }

    /**
     * Get a sub-sequence of this {@link CharSequence}.
     *
     * @param   start   the start index
     * @param   end     the end index
     * @return  the sub-sequence
     * @throws  IllegalArgumentException if the start or end index is invalid
     * @see     CharSequence#subSequence(int, int)
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return new SubSequence(this, start, end);
    }

    /**
     * Get the string from this {@link CharSequence}.  The result is stored for subsequent
     * re-use.
     *
     * @return  the string containing all the characters of this {@link CharSequence}
     * @see     Object#toString()
     */
    @Override
    public String toString() {
        if (str == null) {

            // create output StringBuffer

            StringBuilder sb = new StringBuilder(length);

            // append buffers 0 to n-1

            int lastbuffer = buffers.length - 1;
            for (int i = 0; i < lastbuffer; i++)
                sb.append(buffers[i]);

            // append last buffer (if there is one)

            if (lastbuffer >= 0)
                sb.append(buffers[lastbuffer], 0, length - lastbuffer * bufferSize);
            str = sb.toString();
        }
        return str;
    }

    /**
     * Convenience method to convert the contents of a {@link Reader} to a {@link String}.
     *
     * @param   rdr     the {@link Reader}
     * @return          the {@link String}
     * @throws IOException if thrown by the {@link Reader}
     */
    public static String toString(Reader rdr) throws IOException {
        return (new ReaderBuffer(rdr)).toString();
    }

    /**
     * Convenience method to convert the contents of a {@link Reader} to a {@link String} using
     * a specified buffer size.
     *
     * @param   rdr         the {@link Reader}
     * @param   bufferSize  the buffer size
     * @return              the {@link String}
     * @throws IOException if thrown by the {@link Reader}
     */
    public static String toString(Reader rdr, int bufferSize) throws IOException {
        return (new ReaderBuffer(rdr, bufferSize)).toString();
    }

}
