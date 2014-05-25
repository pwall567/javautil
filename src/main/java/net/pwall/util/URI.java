/*
 * @(#) URI.java
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
 * A set of static methods to assist with URIs.
 *
 * @author Peter Wall
 */
public class URI {

    private static final CharMapper charMapper = new AbstractCharMapper() {
        @Override
        public String map(int codePoint) {
            if (codePoint > 0xFF)
                throw new IllegalArgumentException("Character outside range");
            if (!isUnreserved((char)codePoint))
                return hexMapping(codePoint, 2, "%", null);
            return null;
        }
    };

    /**
     * Test whether a given character is "unreserved" according to the URI specification
     * <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>.
     *
     * @param ch    the character to be tested
     * @return      {@code true} if the character is unreserved
     */
    public static boolean isUnreserved(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' ||
                ch == '-' || ch == '.' || ch == '_' || ch == '~';
    }

    /**
     * Escape a string for use in a URI.  Only subcomponents of a URI (e.g. path elements,
     * parameter names and values etc.) should be encoded, NOT the URI as a whole.
     *
     * @param s     the string to be escaped
     * @return      the escaped string
     */
    public static String escape(String s) {
        return Strings.escape(s, charMapper);
    }

}
