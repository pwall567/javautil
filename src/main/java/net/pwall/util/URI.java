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
        public String map(int cp) {
            if (cp > 0x7F) {
                StringBuilder sb = new StringBuilder();
                ByteArrayBuilder bab = new ByteArrayBuilder();
                Strings.appendUTF8(bab, cp);
                for (int i = 0; i < bab.length(); i++)
                    sb.append(hexMapping(bab.get(i), 2, "%", null));
                return sb.toString();
            }
            if (!isUnreserved(cp))
                return hexMapping(cp, 2, "%", null);
            return null;
        }
    };

    /**
     * Test whether a given code point is "unreserved" according to the URI specification
     * <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>.
     *
     * @param cp    the code point to be tested
     * @return      {@code true} if the character is unreserved
     */
    public static boolean isUnreserved(int cp) {
        return cp >= 'A' && cp <= 'Z' || cp >= 'a' && cp <= 'z' || cp >= '0' && cp <= '9' ||
                cp == '-' || cp == '.' || cp == '_' || cp == '~';
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
