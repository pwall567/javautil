/*
 * @(#) URI.java
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

/**
 * A set of static methods to assist with URIs.
 *
 * @author Peter Wall
 */
public class URI {

    public static final String errorMessage = "URI %xx sequence invalid";

    /** A {@link CharMapper} for use with {@link Strings#escape(String, CharMapper)} etc. */
    public static final CharMapper charMapper = cp -> {
        if (cp > 0x7F) {
            StringBuilder sb = new StringBuilder();
            ByteArrayBuilder bab = new ByteArrayBuilder();
            Strings.appendUTF8(bab, cp);
            for (int i = 0; i < bab.length(); i++)
                sb.append(CharMapper.hexMapping(bab.get(i), 2, "%"));
            return sb.toString();
        }
        if (cp == ' ')
            return "+";
        if (!isUnreserved(cp))
            return CharMapper.hexMapping(cp, 2, "%", null);
        return null;
    };

    /** A {@link CharUnmapper} for use with {@link Strings#unescape(String, CharUnmapper)}
        etc. */
    public static final CharUnmapper charUnmapper = new CharUnmapper() {
        @Override
        public boolean isEscape(CharSequence s, int offset) {
            char ch = s.charAt(offset);
            return ch == '%' || ch == '+';
        }
        @Override
        public int unmap(StringBuilder sb, CharSequence s, int offset) {
            if (s.charAt(offset) == '+') {
                sb.append(' ');
                return 1;
            }
            if (offset + 3 <= s.length()) {
                sb.append((char)(convertHexDigit(s.charAt(offset + 1)) * 16 +
                        convertHexDigit(s.charAt(offset + 2))));
                return 3;
            }
            throw new IllegalArgumentException(errorMessage);
        }
    };

    /**
     * Private constructor to prevent instantiation.  Attempts to instantiate the class via
     * reflection will cause an {@link IllegalAccessException}.
     *
     * @throws  IllegalAccessException in all cases
     */
    private URI() throws IllegalAccessException {
        throw new IllegalAccessException("Attempt to instantiate URI class");
    }

    /**
     * Test whether a given code point is "unreserved" according to the URI specification
     * <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>.
     *
     * @param   cp  the code point to be tested
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
     * @param   s   the string to be escaped
     * @return      the escaped string
     */
    public static String escape(String s) {
        return Strings.escape(s, charMapper);
    }

    /**
     * Unescape a string from a URI.
     *
     * @param   s   the string to be converted
     * @return      the "unescaped" string
     */
    public static String unescape(String s) {
        return Strings.unescape(s, charUnmapper);
    }

    /**
     * Convert a hex digit to {@code int}.
     *
     * @param   ch  the character containg the hex digit
     * @return      the {@code int}
     * @throws      IllegalArgumentException if the character is not a hex digit
     */
    protected static int convertHexDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        throw new IllegalArgumentException(errorMessage);
    }

}
