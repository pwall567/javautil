/*
 * @(#) Java.java
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

import java.io.IOException;

/**
 * Static methods for working with Java source code.
 *
 * @author Peter Wall
 *
 */
public class Java {

    public static final CharMapper stringMapper = new AbstractCharMapper() {
        @Override
        public String map(int codePoint) {
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
            return hexMapping(codePoint, 4, "\\u");
        }
    };

    public static final CharMapper charMapper = new AbstractCharMapper() {
        @Override
        public String map(int codePoint) {
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
            return hexMapping(codePoint, 4, "\\u");
        }
    };

    /**
     * Private constructor - class is not to be instantiated.
     */
    private Java() {
    }

    public static String quote(String str) {
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

    public static CharSequence trim(CharSequence str) {
        int i = 0, n = str.length();
        while (i < n && Character.isWhitespace(str.charAt(i)))
            i++;
        while (i < n && Character.isWhitespace(str.charAt(n - 1)))
            n--;
        return i == n ? "" : i == 0 && n == str.length() ? str : str.subSequence(i, n);
    }

    public static String trim(String str) {
        int i = 0, n = str.length();
        while (i < n && Character.isWhitespace(str.charAt(i)))
            i++;
        while (i < n && Character.isWhitespace(str.charAt(n - 1)))
            n--;
        return i == n ? "" : i == 0 && n == str.length() ? str : str.substring(i, n);
    }

    public static boolean isAllSpace(CharSequence str) {
        for (int i = 0, n = str.length(); i < n; i++)
            if (!Character.isWhitespace(str.charAt(i)))
                return false;
        return true;
    }

}
