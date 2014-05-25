/*
 * @(#) CharUnmapper.java
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
 * Character unmapper interface for "unescape" functions.
 *
 * @author  Peter Wall
 * @see     Strings#unescape(CharSequence, CharUnmapper)
 * @see     Strings#unescape(String, CharUnmapper)
 */
public interface CharUnmapper {

    /**
     * Test whether the character at {@code offset} in the {@link CharSequence} {@code s} is the
     * start of an escape sequence.
     *
     * @param s         the {@link CharSequence} containing the input
     * @param offset    the offset within the input
     * @return          {@code true} if the character is the start of an escape
     * @see             Strings#unescape(String, CharUnmapper)
     * @see             Strings#unescape(CharSequence, CharUnmapper)
     */
    boolean isEscape(CharSequence s, int offset);

    /**
     * Map the characters starting at {@code offset} in the {@link CharSequence} {@code s} with
     * any of the possible escape sequences. If successful, store the character (or possibly the
     * two-character surrogate sequence) in {@link StringBuilder} {@code sb} and return the
     * length of the sequence; otherwise return {@code 0} to indicate no match.
     *
     * @param sb        the {@link StringBuilder} in which to store the output
     * @param s         the {@link CharSequence} containing the input
     * @param offset    the offset within the input
     * @return          the length of the mapped sequence
     * @throws          IllegalArgumentException if the escape sequence is invalid
     * @see             Strings#unescape(String, CharUnmapper)
     * @see             Strings#unescape(CharSequence, CharUnmapper)
     */
    int unmap(StringBuilder sb, CharSequence s, int offset);

}
