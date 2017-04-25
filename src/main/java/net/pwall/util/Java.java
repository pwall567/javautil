/*
 * @(#) Java.java
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

/**
 * Static methods for working with Java source code.
 *
 * @author Peter Wall
 *
 */
public class Java {

    public static final CharMapper stringMapper = codePoint -> {
        if (codePoint == '"')
            return "\\\"";
        if (codePoint == '\\')
            return "\\\\";
        if (codePoint >= ' ' && codePoint < 0x7F)
            return null;
        if (codePoint == '\n')
            return "\\n";
        if (codePoint == '\r')
            return "\\r";
        if (codePoint == '\t')
            return "\\t";
        if (codePoint == '\b')
            return "\\b";
        if (codePoint == '\f')
            return "\\f";
        return CharMapper.hexMapping(codePoint, 4, "\\u");
    };

    public static final CharMapper charMapper = codePoint -> {
        if (codePoint == '\'')
            return "\\'";
        if (codePoint == '\\')
            return "\\\\";
        if (codePoint >= ' ' && codePoint < 0x7F)
            return null;
        if (codePoint == '\n')
            return "\\n";
        if (codePoint == '\r')
            return "\\r";
        if (codePoint == '\t')
            return "\\t";
        if (codePoint == '\b')
            return "\\b";
        if (codePoint == '\f')
            return "\\f";
        return CharMapper.hexMapping(codePoint, 4, "\\u");
    };

    /**
     * Private constructor - class is not to be instantiated.
     */
    private Java() {
    }

    /**
     * Convert a string to the quoted form as used in Java source code, converting non-ASCII
     * characters as appropriate.  A {@code null} string will be converted to "{@code null}".
     *
     * @param   str     the original string
     * @return          the quoted form
     */
    public static String quote(String str) {
        if (str == null)
            return "null";
        StringBuilder sb = new StringBuilder(str.length() + 8); // allow a few extra
        sb.append('"');
        try {
            Strings.appendEscaped(sb, str, stringMapper);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Convert a character to the quoted form as used in Java source code, converting non-ASCII
     * characters as appropriate.
     *
     * @param   ch      the character
     * @return          the quoted form
     */
    public static String quoteCharacter(char ch) {
        StringBuilder sb = new StringBuilder(8); // allow for maximum
        sb.append('\'');
        String mapped = charMapper.map(ch);
        if (mapped != null)
            sb.append(mapped);
        else
            sb.append(ch);
        sb.append('\'');
        return sb.toString();
    }

    /**
     * Trim whitespace from the start and end of a {@link CharSequence}, using the Java source
     * code rules for whitespace.
     *
     * @param   cs      the {@link CharSequence}
     * @return          the trimmed {@link CharSequence}
     */
    public static CharSequence trim(CharSequence cs) {
        return Strings.trim(cs, Character::isWhitespace);
    }

    /**
     * Trim whitespace from the start and end of a {@link String}, using the Java source code
     * rules for whitespace.
     *
     * @param   str     the {@link String}
     * @return          the trimmed {@link String}
     */
    public static String trim(String str) {
        return Strings.trim(str, Character::isWhitespace);
    }

    /**
     * Test whether a {@link CharSequence} contains all whitespace, using the Java source code
     * rules for whitespace.
     *
     * @param   cs      the {@link CharSequence}
     * @return          {@code true} if the {@link CharSequence} contains all whitespace
     */
    public static boolean isAllSpace(CharSequence cs) {
        for (int i = 0, n = cs.length(); i < n; i++)
            if (!Character.isWhitespace(cs.charAt(i)))
                return false;
        return true;
    }

}
