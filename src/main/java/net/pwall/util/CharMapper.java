/*
 * @(#) CharMapper.java
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
 * Character mapper interface for "escape" functions.
 *
 * @author  Peter Wall
 * @see     Strings#escape(CharSequence, CharMapper)
 * @see     Strings#escape(String, CharMapper)
 * @see     Strings#escapeUTF16(CharSequence, CharMapper)
 * @see     Strings#escapeUTF16(String, CharMapper)
 */
@FunctionalInterface
public interface CharMapper {

    /**
     * Map character to it's "escaped" string equivalent, for example '+' to "%2B" in URL
     * encoding.  Return {@code null} if the character does not need to be converted.
     *
     * @param   codePoint   the Unicode code point of the character to be mapped
     * @return              the escaped string equivalent, or {@code null} if no escape needed
     */
    String map(int codePoint);

}
