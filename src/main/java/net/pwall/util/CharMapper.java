/*
 * @(#) CharMapper.java
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

    /**
     * Map a range of contiguous code points into strings using an array.  The first entry in
     * the array represents the code point specified by the base, the second is base + 1 etc.
     *
     * @param array     the array of strings
     * @param codePoint the Unicode code point
     * @param base      the code point representing the first entry in the array
     * @return          the string representing the code point, or {@code null} if the code
     *                  point is outside the range
     */
    static String arrayMapping(String[] array, int codePoint, int base) {
        if (codePoint >= base && codePoint < base + array.length)
            return array[codePoint - base];
        return null;
    }

    /**
     * Map a code point using a lookup table.  The lookup uses a binary search so the table
     * entries must be in ascending order by code point.
     *
     * @param table     an array of mapping entries, each of which is an array of two strings:
     *                  a single character string containing the character to be mapped, and the
     *                  result string
     * @param codePoint the Unicode code point
     * @return          the string representing the code point, or {@code null} if no entry
     *                  exists in the table for the code point
     */
    static String lookupMapping(String[][] table, int codePoint) {
        int hi = table.length;
        if (hi > 0 && codePoint >= table[0][0].charAt(0) &&
                codePoint <= table[hi - 1][0].charAt(0)) {
            int lo = 0;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                String[] entry = table[mid];
                char ch = entry[0].charAt(0);
                if (codePoint == ch)
                    return entry[1];
                if (codePoint < ch)
                    hi = mid;
                else
                    lo = mid + 1;
            }
        }
        return null;
    }

    /**
     * Map a code point using a lookup table.  The lookup uses a binary search so the table
     * entries must be in ascending order by code point.
     *
     * @param table     an array of mapping entries, each of which is an array of two strings:
     *                  a single character string containing the character to be mapped, and the
     *                  result string
     * @param codePoint the Unicode code point
     * @return          the string representing the code point, or {@code null} if no entry
     *                  exists in the table for the code point
     */
    static String lookupMapping(CharMapperEntry[] table, int codePoint) {
        int hi = table.length;
        if (hi > 0 && codePoint >= table[0].getCodePoint() &&
                codePoint <= table[hi - 1].getCodePoint()) {
            int lo = 0;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                CharMapperEntry entry = table[mid];
                int entryCodePoint = entry.getCodePoint();
                if (codePoint == entryCodePoint)
                    return entry.getString();
                if (codePoint < entryCodePoint)
                    hi = mid;
                else
                    lo = mid + 1;
            }
        }
        return null;
    }

    /**
     * Map a code point into a decimal string with prefix and optional suffix.
     *
     * @param codePoint the Unicode code point
     * @param prefix    the prefix string
     * @param suffix    the suffix string (or {@code null} if no suffix needed)
     * @return          the decimal representation of the code point, with the specified prefix
     *                  and optional suffix
     */
    static String decimalMapping(int codePoint, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder(prefix.length() + 7 +
                (suffix != null ? suffix.length() : 0));
        sb.append(prefix);
        try {
            Strings.appendPositiveInt(sb, codePoint);
        }
        catch (IOException e) {
            // can't happen - StringBuilder doesn't throw IOException
        }
        if (suffix != null)
            sb.append(suffix);
        return sb.toString();
    }

    /**
     * Map a code point into a fixed-length hexadecimal string with prefix and optional suffix.
     *
     * @param codePoint the Unicode code point
     * @param length    the number of hexadecimal digits
     * @param prefix    the prefix string
     * @param suffix    the suffix string (or {@code null} if no suffix needed)
     * @return          the hexadecimal representation of the code point, zero-padded to the
     *                  required number of digits, with the specified prefix and optional suffix
     */
    static String hexMapping(int codePoint, int length, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder(prefix.length() + length +
                (suffix != null ? suffix.length() : 0));
        sb.append(prefix);
        try {
            Strings.appendHex(sb, codePoint, length);
        }
        catch (IOException ioe) {
            // can't happen - StringBuilder doesn't throw IOException
        }
        if (suffix != null)
            sb.append(suffix);
        return sb.toString();
    }

    /**
     * Map a code point into a fixed-length hexadecimal string with prefix and no suffix.  This
     * is an optimization of the common case of no suffix being required.
     *
     * @param codePoint the Unicode code point
     * @param length    the number of hexadecimal digits
     * @param prefix    the prefix string
     * @return          the hexadecimal representation of the code point, zero-padded to the
     *                  required number of digits, with the specified prefix
     */
    static String hexMapping(int codePoint, int length, String prefix) {
        StringBuilder sb = new StringBuilder(prefix.length() + length);
        sb.append(prefix);
        try {
            Strings.appendHex(sb, codePoint, length);
        }
        catch (IOException ioe) {
            // can't happen - StringBuilder doesn't throw IOException
        }
        return sb.toString();
    }

}
