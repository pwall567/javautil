/*
 * @(#) Strings.java
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
import java.util.Enumeration;
import java.util.Iterator;

/**
 * String utility functions.
 *
 * @author Peter Wall
 */
public class Strings {

    private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F' };

    private static final String emptyString = "";
    private static final String[] emptyStringArray = {};

    public static String[] splitRecursive(String s) {
        return splitRecursive(s, 0, s.length(), 0);
    }

    public static String[] splitRecursive(String s, int start, int end) {
        return splitRecursive(s, start, end, 0);
    }

    public static String[] splitRecursive(String s, int i, int end, int offset) {
        for (;;) {
            if (i >= end)
                return offset == 0 ? emptyStringArray : new String[offset];
            if (!Character.isWhitespace(s.charAt(i)))
                break;
            i++;
        }
        int tokenStart = i;
//        for (;;) {
//            if (++i >= end) {
//                String[] result = new String[offset + 1];
//                result[offset] = s.substring(tokenStart, i);
//                return result;
//            }
//            if (Character.isWhitespace(s.charAt(i))) {
//                String[] result = splitRecursive(s, i + 1, end, offset + 1);
//                result[offset] = s.substring(tokenStart, i);
//                return result;
//            }
//        }
        int j = i;
        int k = end;
        while (++j < k && !Character.isWhitespace(s.charAt(j)))
            ;
        String[] result = j < k ? splitRecursive(s, j + 1, k, offset + 1) :
                new String[offset + 1];
        result[offset] = s.substring(tokenStart, j);
        return result;
    }

    /**
     * Split a string into white space delimited tokens, where white space is determined by
     * {@link Character#isWhitespace(char)}.
     *
     * @param s  the string to be split
     * @return   an array of tokens (possibly empty)
     * @throws   NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s) {
        return split(s, 0, s.length(), defaultSpaceTest);
    }

    /**
     * Split a portion of a string into white space delimited tokens, where white space is
     * determined by {@link Character#isWhitespace(char)}.
     *
     * @param s      the string to be split
     * @param start  the start index of the portion to be examined
     * @param end    the end index (exclusive) of the portion to be examined
     * @return       an array of tokens (possibly empty)
     * @throws       NullPointerException if the input string is {@code null}
     * @throws       IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end) {
//        for (;;) {
//            if (start >= end)
//                return emptyStringArray;
//            if (!Character.isWhitespace(s.charAt(start)))
//                break;
//            start++;
//        }
//        while (Character.isWhitespace(s.charAt(--end)))
//            ;
//        int count = 0, i = start + 1;
//    outer:
//        for (;;) {
//            for (;;) {
//                if (i >= end)
//                    break outer;
//                if (Character.isWhitespace(s.charAt(i++)))
//                    break;
//            }
//            count++;
//            while (Character.isWhitespace(s.charAt(i++)))
//                ;
//        }
//        String[] result = new String[count + 1];
//        i = start;
//        for (int j = 0; j < count; j++) {
//            int k = i;
//            do {
//                i++;
//            } while (!Character.isWhitespace(s.charAt(i)));
//            result[j] = s.substring(k, i);
//            do {
//                i++;
//            } while (Character.isWhitespace(s.charAt(i)));
//        }
//        result[count] = s.substring(i, end + 1);
//        return result;
        return split(s, start, end, defaultSpaceTest);
    }

    /**
     * Split a string into white space delimited tokens, where white space is determined by a
     * supplied {@link SpaceTest} object.  In Java 8 lambda notation, this may be called by:
     * <pre>
     *     String[] array = Strings.split("a b c d e", ch -> Character.isSpace(ch));
     * </pre>
     *
     * @param   s       the string to be split
     * @return          an array of tokens (possibly empty)
     * @throws          NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s, SpaceTest st) {
        return split(s, 0, s.length(), st);
    }

    /**
     * Split a portion of a string into white space delimited tokens, where white space is
     * determined by a supplied {@link SpaceTest} object.
     *
     * @param   s       the string to be split
     * @param   start   the start index of the portion to be examined
     * @param   end     the end index (exclusive) of the portion to be examined
     * @return          an array of tokens (possibly empty)
     * @throws          NullPointerException if the input string is {@code null}
     * @throws          IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end, SpaceTest st) {
        for (;;) {
            if (start >= end)
                return emptyStringArray;
            if (!st.isSpace(s.charAt(start)))
                break;
            start++;
        }
        while (st.isSpace(s.charAt(--end)))
            ;
        int count = 0, i = start + 1;
    outer:
        for (;;) {
            for (;;) {
                if (i >= end)
                    break outer;
                if (st.isSpace(s.charAt(i++)))
                    break;
            }
            count++;
            while (st.isSpace(s.charAt(i++)))
                ;
        }
        String[] result = new String[count + 1];
        i = start;
        for (int j = 0; j < count; j++) {
            int k = i;
            do {
                i++;
            } while (!st.isSpace(s.charAt(i)));
            result[j] = s.substring(k, i);
            do {
                i++;
            } while (st.isSpace(s.charAt(i)));
        }
        result[count] = s.substring(i, end + 1);
        return result;
    }

    /**
     * An interface to define a space test for the associated {@code split()} functions.  The
     * interface is named {@code SpaceTest} rather than {@code SeparatorTest} because the
     * functions that use it treat multiple occurences as equivalent to a single character -
     * this is generally the required behaviour for spaces but not for, say, commas.
     * 
     * @see     Strings#split(String, SpaceTest)
     * @see     Strings#split(String, int, int, SpaceTest)
     */
    //@FunctionalInterface /* TODO uncomment this when Java 8 is available */
    public static interface SpaceTest {
        boolean isSpace(char ch);
    }

    public static final SpaceTest defaultSpaceTest = new SpaceTest() {
        @Override
        public boolean isSpace(char ch) {
            return Character.isWhitespace(ch);
        }
    };

    /**
     * Split a string on a given separator.
     *
     * @param   s           the string to be split
     * @param   separator   the separator
     * @return              an array of items (possibly empty)
     * @throws              NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s, char separator) {
        return split(s, 0, s.length(), separator, true, true);
    }

    /**
     * Split a string on a given separator.
     *
     * @param   s           the string to be split
     * @param   separator   the separator
     * @param   trim        if {@code true}, trim spaces off both ends of each item
     * @param   skipEmpty   if {@code true}, ignore zero-length items (possibly after trimming)
     * @return              an array of items (possibly empty)
     * @throws              NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s, char separator, boolean trim, boolean skipEmpty) {
        return split(s, 0, s.length(), separator, trim, skipEmpty);
    }

    /**
     * Split a portion of a string on a given separator.
     *
     * @param   s           the string to be split
     * @param   start       the start index of the portion to be examined
     * @param   end         the end index (exclusive) of the portion to be examined
     * @param   separator   the separator
     * @param   trim        if {@code true}, trim spaces off both ends of each item
     * @param   skipEmpty   if {@code true}, ignore zero-length items (possibly after trimming)
     * @return              an array of items (possibly empty)
     * @throws              NullPointerException if the input string is {@code null}
     * @throws              IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end, char separator, boolean trim,
            boolean skipEmpty) {
        int count = 0;
        int i = start;
        if (skipEmpty) {
            if (trim) {
                // count the number of items (ignoring zero-length or all space items)
                for (;;) {
                    boolean nonSpaceSeen = false;
                    while (i < end) {
                        char ch = s.charAt(i);
                        if (ch == separator)
                            break;
                        nonSpaceSeen = nonSpaceSeen || !Character.isWhitespace(ch);
                        i++;
                    }
                    if (nonSpaceSeen)
                        count++;
                    if (i >= end)
                        break;
                    i++;
                }
            }
            else {
                // count the number of items (ignoring zero-length items)
                for (;;) {
                    int itemStart = i;
                    while (i < end && s.charAt(i) != separator)
                        i++;
                    if (i > itemStart)
                        count++;
                    if (i >= end)
                        break;
                    i++;
                }
            }
            if (count == 0)
                return emptyStringArray;
        }
        else {
            // otherwise, count is just (number of separators + 1)
            count = 1;
            while (i < end) {
                if (s.charAt(i++) == separator)
                    count++;
            }
        }
        String[] result = new String[count];
        i = start;
        int j = 0;
        for (;;) {
            int itemStart = i;
            while (i < end && s.charAt(i) != separator)
                i++;
            int itemEnd = i;
            if (trim) {
                while (itemStart < itemEnd && Character.isWhitespace(s.charAt(itemStart)))
                    itemStart++;
                while (itemStart < itemEnd && Character.isWhitespace(s.charAt(itemEnd - 1)))
                    itemEnd--;
            }
            if (itemEnd > itemStart)
                result[j++] = s.substring(itemStart, itemEnd);
            else if (!skipEmpty)
                result[j++] = emptyString;
            if (i >= end)
                break;
            i++;
        }
        return result;
    }

    /**
     * Join the string representations of the members of a collection.
     *
     * @param   collection  the collection (strictly speaking, an {@link Iterable})
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the collection is empty)
     */
    public static <E> String join(Iterable<E> collection) {
        return join(collection.iterator());
    }

    /**
     * Join the string representations of the members of an {@link Iterator}.
     *
     * @param   it      the {@link Iterator}
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Iterator} has no members)
     */
    public static <E> String join(Iterator<E> it) {
        if (!it.hasNext())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(it.next());
        } while (it.hasNext());
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an {@link Enumeration}.
     *
     * @param   e       the {@link Enumeration}
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Enumeration} has no members)
     */
    public static <E> String join(Enumeration<E> e) {
        if (!e.hasMoreElements())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(e.nextElement());
        } while (e.hasMoreElements());
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an array.
     *
     * @param   array   the array
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the array is empty)
     */
    public static <E> String join(E[] array) {
        int n = array.length;
        if (n == 0)
            return emptyString;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(array[i++]);
        } while (i < n);
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of a collection, with the specified
     * character separator.
     *
     * @param   collection  the collection (strictly speaking, an {@link Iterable})
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the collection is empty)
     */
    public static <E> String join(Iterable<E> collection, char separator) {
        return join(collection.iterator(), separator);
    }

    /**
     * Join the string representations of the members of an {@link Iterator}, with the specified
     * character separator.
     *
     * @param   it          the {@link Iterator}
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Iterator} has no members)
     */
    public static <E> String join(Iterator<E> it, char separator) {
        if (!it.hasNext())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(it.next());
            if (!it.hasNext())
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an {@link Enumeration}, with the
     * specified character separator.
     *
     * @param   e           the {@link Enumeration}
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Enumeration} has no members)
     */
    public static <E> String join(Enumeration<E> e, char separator) {
        if (!e.hasMoreElements())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(e.nextElement());
            if (!e.hasMoreElements())
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an array, with the specified character
     * separator.
     *
     * @param   array       the array
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the array is empty)
     */
    public static <E> String join(E[] array, char separator) {
        int n = array.length;
        if (n == 0)
            return emptyString;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(array[i++]);
            if (i >= n)
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of a collection, with the specified string
     * separator.
     *
     * @param   collection  the collection (strictly speaking, an {@link Iterable})
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the collection is empty)
     */
    public static <E> String join(Iterable<E> collection, String separator) {
        return join(collection.iterator(), separator);
    }

    /**
     * Join the string representations of the members of an {@link Iterator}, with the specified
     * string separator.
     *
     * @param   it          the {@link Iterator}
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Iterator} has no members)
     */
    public static <E> String join(Iterator<E> it, String separator) {
        if (!it.hasNext())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(it.next());
            if (!it.hasNext())
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an {@link Enumeration}, with the
     * specified string separator.
     *
     * @param   e           the {@link Enumeration}
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Enumeration} has no members)
     */
    public static <E> String join(Enumeration<E> it, String separator) {
        if (!it.hasMoreElements())
            return emptyString;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(it.nextElement());
            if (!it.hasMoreElements())
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Join the string representations of the members of an array, with the specified string
     * separator.
     *
     * @param   array       the array
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the array is empty)
     */
    public static <E> String join(E[] array, String separator) {
        int n = array.length;
        if (n == 0)
            return emptyString;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(array[i++]);
            if (i >= n)
                break;
            sb.append(separator);
        }
        return sb.length() == 0 ? emptyString : sb.toString();
    }

    /**
     * Replace certain characters in a string with their mapped equivalents, as specified in the
     * provided {@link CharMapper} instance.  If the string contains no characters to be mapped,
     * the original string is returned unmodified.
     *
     * @param   s       the string to be converted
     * @param   mapper  the {@link CharMapper} instance
     * @return  the string with characters mapped as required
     */
    public static final String escape(String s, CharMapper mapper) {
        for (int i = 0, n = s.length(); i < n; ) {
            char ch = s.charAt(i++);
            String mapped = mapper.map(ch);
            if (mapped != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(s, 0, i - 1);
                sb.append(mapped);
                try {
                    appendEscaped(sb, s, i, n, mapper);
                }
                catch (IOException e) {
                    // can't happen - StringBuilder.append() does not throw IOException
                }
                return sb.toString();
            }
        }
        return s;
    }

    /**
     * Replace certain characters in a {@link CharSequence} with their mapped equivalents, as
     * specified in the provided {@link CharMapper} instance.  If the sequence contains no
     * characters to be mapped, the original sequence is returned unmodified.
     *
     * @param   s       the {@link CharSequence} to be converted
     * @param   mapper  the {@link CharMapper} instance
     * @return  the sequence with characters mapped as required
     */
    public static final CharSequence escape(CharSequence s, CharMapper mapper) {
        for (int i = 0, n = s.length(); i < n; ) {
            char ch = s.charAt(i++);
            String mapped = mapper.map(ch);
            if (mapped != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(s, 0, i - 1);
                sb.append(mapped);
                try {
                    appendEscaped(sb, s, i, n, mapper);
                }
                catch (IOException e) {
                    // can't happen - StringBuilder.append() does not throw IOException
                }
                return sb;
            }
        }
        return s;
    }

    /**
     * Append characters to an {@link Appendable}, mapping them to their "escaped" equivalents
     * specified in the provided {@link CharMapper} instance.
     *
     * @param   a       the {@link Appendable} (e.g. a {@link StringBuilder})
     * @param   s       the source {@link CharSequence}
     * @param   index   the start index within the source
     * @param   end     the end index within the source
     * @param   mapper  the {@link CharMapper}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static final void appendEscaped(Appendable a, CharSequence s, int index, int end,
            CharMapper mapper) throws IOException {
        while (index < end) {
            char ch = s.charAt(index++);
            String mapped = mapper.map(ch);
            if (mapped != null)
                a.append(mapped);
            else
                a.append(ch);
        }
    }

    /**
     * Append characters to an {@link Appendable}, mapping them to their "escaped" equivalents
     * specified in the provided {@link CharMapper} instance.
     *
     * @param   a       the {@link Appendable} (e.g. a {@link StringBuilder})
     * @param   s       the source {@link CharSequence}
     * @param   mapper  the {@link CharMapper}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static final void appendEscaped(Appendable a, CharSequence s, CharMapper mapper)
            throws IOException {
        appendEscaped(a, s, 0, s.length(), mapper);
    }

    /**
     * Replace certain characters in a string with their mapped equivalents, as specified in the
     * provided {@link CharMapper} instance.  Surrogate sequences are converted to Unicode
     * code points before mapping.  If the string contains no characters to be mapped, the
     * original string is returned unmodified.
     *
     * @param   s       the UTF16 string to be converted
     * @param   mapper  the {@link CharMapper} instance
     * @return  the string with characters mapped as required
     */
    public static final String escapeUTF16(String s, CharMapper mapper) {
        for (int i = 0, n = s.length(); i < n; ) {
            int k = i;
            char ch1 = s.charAt(i++);
            String mapped;
            if (Character.isHighSurrogate(ch1)) {
                char ch2;
                if (i >= n || !Character.isLowSurrogate(ch2 = s.charAt(i++)))
                    throw new IllegalArgumentException("Illegal surrogate sequence");
                mapped = mapper.map(Character.toCodePoint(ch1, ch2));
            }
            else
                mapped = mapper.map(ch1);
            if (mapped != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(s, 0, k);
                sb.append(mapped);
                try {
                    appendEscapedUTF16(sb, s, i, n, mapper);
                }
                catch (IOException e) {
                    // can't happen - StringBuilder.append() does not throw IOException
                }
                return sb.toString();
            }
        }
        return s;
    }

    /**
     * Replace certain characters in a {@link CharSequence} with their mapped equivalents, as
     * specified in the provided {@link CharMapper} instance.  Surrogate sequences are converted
     * to Unicode code points before mapping.  If the sequence contains no characters to be
     * mapped, the original sequence is returned unmodified.
     *
     * @param   s       the {@link CharSequence} to be converted
     * @param   mapper  the {@link CharMapper} instance
     * @return  the sequence with characters mapped as required
     */
    public static final CharSequence escapeUTF16(CharSequence s, CharMapper mapper) {
        for (int i = 0, n = s.length(); i < n; ) {
            int k = i;
            char ch1 = s.charAt(i++);
            String mapped;
            if (Character.isHighSurrogate(ch1)) {
                char ch2;
                if (i >= n || !Character.isLowSurrogate(ch2 = s.charAt(i++)))
                    throw new IllegalArgumentException("Illegal surrogate sequence");
                mapped = mapper.map(Character.toCodePoint(ch1, ch2));
            }
            else
                mapped = mapper.map(ch1);
            if (mapped != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(s, 0, k);
                sb.append(mapped);
                try {
                    appendEscapedUTF16(sb, s, i, n, mapper);
                }
                catch (IOException e) {
                    // can't happen - StringBuilder.append() does not throw IOException
                }
                return sb;
            }
        }
        return s;
    }

    /**
     * Append characters to an {@link Appendable}, mapping them to their "escaped" equivalents
     * specified in the provided {@link CharMapper} instance.  Surrogate sequences are converted
     * to Unicode code points before mapping.
     *
     * @param   a       the {@link Appendable} (e.g. a {@link StringBuilder})
     * @param   s       the source {@link CharSequence}
     * @param   index   the start index within the source
     * @param   end     the end index within the source
     * @param   mapper  the {@link CharMapper}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static final void appendEscapedUTF16(Appendable a, CharSequence s, int index,
            int end, CharMapper mapper) throws IOException {
        String mapped;
        while (index < end) {
            char ch1 = s.charAt(index++);
            if (Character.isHighSurrogate(ch1)) {
                char ch2;
                if (index >= end || !Character.isLowSurrogate(ch2 = s.charAt(index++)))
                    throw new IllegalArgumentException("Illegal surrogate sequence");
                mapped = mapper.map(Character.toCodePoint(ch1, ch2));
                if (mapped != null)
                    a.append(mapped);
                else
                    a.append(ch1).append(ch2);
            }
            else {
                mapped = mapper.map(ch1);
                if (mapped != null)
                    a.append(mapped);
                else
                    a.append(ch1);
            }
        }
    }

    /**
     * Append characters to an {@link Appendable}, mapping them to their "escaped" equivalents
     * specified in the provided {@link CharMapper} instance.  Surrogate sequences are converted
     * to Unicode code points before mapping.
     *
     * @param   a       the {@link Appendable} (e.g. a {@link StringBuilder})
     * @param   s       the source {@link CharSequence}
     * @param   mapper  the {@link CharMapper}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static final void appendEscapedUTF16(Appendable a, CharSequence s, CharMapper mapper)
            throws IOException {
        appendEscapedUTF16(a, s, 0, s.length(), mapper);
    }

    /**
     * Scan a string for escape sequences and replace them by the original characters.  For
     * example, in Java code the backslash character indicates the start of an escape sequence
     * representing a character that may not appear in its raw form in a string.  If the
     * string contains no escape sequences to be unmapped, the original sequence is returned
     * unmodified.
     *
     * @param   s           the string to be converted
     * @param   unmapper    an instance of the {@link CharUnmapper} class, which will perform
     *                      the actual escape sequence mapping
     * @return              the "unescaped" string
     */
    public static String unescape(String s, CharUnmapper unmapper) {
        for (int i = 0, n = s.length(); i < n; i++) {
            if (unmapper.isEscape(s, i)) {
                StringBuilder sb = new StringBuilder(s.length());
                sb.append(s, 0, i);
                i += unmapper.unmap(sb, s, i);
                while (i < n) {
                    if (unmapper.isEscape(s, i))
                        i += unmapper.unmap(sb, s, i);
                    else
                        sb.append(s.charAt(i++));
                }
                return sb.toString();
            }
        }
        return s;
    }

    /**
     * Scan a {@link CharSequence} for escape sequences and replace them by the original
     * characters.  For example, in Java code the backslash character indicates the start of an
     * escape sequence representing a character that may not appear in its raw form in a string.
     *
     * @param   s           the {@link CharSequence} to be converted
     * @param   unmapper    an instance of the {@link CharUnmapper} class, which will perform
     *                      the actual escape sequence mapping
     * @return              the "unescaped" {@link CharSequence}
     */
    public static CharSequence unescape(CharSequence s, CharUnmapper unmapper) {
        for (int i = 0, n = s.length(); i < n; i++) {
            if (unmapper.isEscape(s, i)) {
                StringBuilder sb = new StringBuilder(s.length());
                sb.append(s, 0, i);
                i += unmapper.unmap(sb, s, i);
                while (i < n) {
                    if (unmapper.isEscape(s, i))
                        i += unmapper.unmap(sb, s, i);
                    else
                        sb.append(s.charAt(i++));
                }
                return sb;
            }
        }
        return s;
    }

    /**
     * Convert a Unicode code point to a one- or two-character string.
     *
     * @param   codePoint   the Unicode code point
     * @return              the string equivalent
     */
    public static String toUTF16(int codePoint) {
        StringBuilder sb = new StringBuilder(2);
        try {
            appendUTF16(sb, codePoint);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    /**
     * Convert an array of Unicode code points to a string.
     *
     * @param   codePoints  the Unicode code points
     * @return              the string equivalent
     */
    public static String toUTF16(int[] codePoints) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codePoints.length; i++) {
            try {
                appendUTF16(sb, codePoints[i]);
            }
            catch (IOException e) {
                // can't happen - StringBuilder.append() does not throw IOException
            }
        }
        return sb.toString();
    }

    /**
     * Append a Unicode code point to an {@link Appendable}.  Code points outside the BMP are
     * appended as surrogate sequences.
     *
     * @param   a           the {@link Appendable} (e.g. a {@link StringBuilder})
     * @param   codePoint   the Unicode code point
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendUTF16(Appendable a, int codePoint) throws IOException {
        if (Character.isSupplementaryCodePoint(codePoint)) {
            a.append(Character.highSurrogate(codePoint));
            a.append(Character.lowSurrogate(codePoint));
        }
        else if (Character.isBmpCodePoint(codePoint) && !Character.isSurrogate((char)codePoint))
            a.append((char)codePoint);
        else
            throw new IllegalArgumentException("Illegal character for UTF-16");
    }

    /**
     * Convert a string to UTF-8 encoding.
     *
     * @param   str     the string
     * @return          the UTF-8 form of the string (as a byte array)
     */
    public static byte[] toUTF8(String str) {
        // note - expects the string to be encoded in UTF-16
        if (str == null)
            throw new IllegalArgumentException("String must not be null");
        return toUTF8(str, 0, str.length());
    }

    /**
     * Convert a portion of a string to UTF-8 encoding.
     *
     * @param   str     the string
     * @param   start   the start index
     * @param   end     the end index
     * @return          the UTF-8 form of the string (as a byte array)
     */
    public static byte[] toUTF8(String str, int start, int end) {
        // note - expects the string to be encoded in UTF-16
        if (str == null)
            throw new IllegalArgumentException("String must not be null");
        ByteArrayBuilder bab = new ByteArrayBuilder((end - start) * 5 / 4);
        for (int i = start; i < end; i++) {
            char ch = str.charAt(i);
            int codePoint;
            if (Character.isHighSurrogate(ch)) {
                char lowSurrogate;
                if (++i >= end || !Character.isLowSurrogate(lowSurrogate = str.charAt(i)))
                    throw new IllegalArgumentException("Invalid UTF-16 surrogate sequence");
                codePoint = Character.toCodePoint(ch, lowSurrogate);
            }
            else
                codePoint = ch;
            if (codePoint <= 0x7F)
                bab.append(codePoint);
            else if (codePoint <= 0x7FF) {
                bab.append((codePoint >> 6) | 0xC0);
                bab.append((codePoint & 0x3F) | 0x80);
            }
            else if (codePoint <= 0xFFFF) {
                bab.append((codePoint >> 12) | 0xE0);
                bab.append(((codePoint >> 6) & 0x3F) | 0x80);
                bab.append((codePoint & 0x3F) | 0x80);
            }
            else {
                bab.append(((codePoint >> 18) & 0x7) | 0xF0);
                bab.append(((codePoint >> 12) & 0x3F) | 0x80);
                bab.append(((codePoint >> 6) & 0x3F) | 0x80);
                bab.append((codePoint & 0x3F) | 0x80);
            }
        }
        return bab.toByteArray();
    }

    public static String toHex(CharSequence s) {
        if (s == null)
            throw new IllegalArgumentException("argument must not be null");
        int n = s.length();
        if (n == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            try {
                appendHex(sb, s.charAt(i));
            }
            catch (IOException e) {
                // can't happen - StringBuilder.append() does not throw IOException
            }
        }
        return sb.toString();
    }

    public static String toHex(CharSequence s, char separator) {
        if (s == null)
            throw new IllegalArgumentException("argument must not be null");
        int n = s.length();
        if (n == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (;;) {
            try {
                appendHex(sb, s.charAt(i++));
            }
            catch (IOException e) {
                // can't happen - StringBuilder.append() does not throw IOException
            }
            if (i >= n)
                break;
            sb.append(separator);
        }
        return sb.toString();
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null)
            throw new IllegalArgumentException("argument must not be null");
        int n = bytes.length;
        if (n == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            try {
                appendHex(sb, bytes[i]);
            }
            catch (IOException e) {
                // can't happen - StringBuilder.append() does not throw IOException
            }
        }
        return sb.toString();
    }

    public static String toHex(byte[] bytes, char separator) {
        if (bytes == null)
            throw new IllegalArgumentException("argument must not be null");
        int n = bytes.length;
        if (n == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (;;) {
            try {
                appendHex(sb, bytes[i]);
            }
            catch (IOException e) {
                // can't happen - StringBuilder.append() does not throw IOException
            }
            if (i >= n)
                break;
            sb.append(separator);
        }
        return sb.toString();
    }

    public static String toHex(byte b) {
        StringBuilder sb = new StringBuilder(2);
        try {
            appendHex(sb, b);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    public static String toHex(char ch) {
        StringBuilder sb = new StringBuilder(4);
        try {
            appendHex(sb, ch);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    public static String toHex(int i) {
        StringBuilder sb = new StringBuilder(8);
        try {
            appendHex(sb, i);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    public static String toHex(long n) {
        StringBuilder sb = new StringBuilder(16);
        try {
            appendHex(sb, n);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    public static void appendHex(Appendable a, byte b) throws IOException {
        a.append(hexDigits[(b >>> 4) & 0xF]);
        a.append(hexDigits[b & 0xF]);
    }

    public static void appendHex(Appendable a, char ch) throws IOException {
        appendHex(a, (byte)(ch >>> 8));
        appendHex(a, (byte)ch);
    }

    public static void appendHex(Appendable a, int i) throws IOException {
        appendHex(a, (char)(i >>> 16));
        appendHex(a, (char)i);
    }

    public static void appendHex(Appendable a, long n) throws IOException {
        appendHex(a, (int)(n >>> 32));
        appendHex(a, (int)n);
    }

    /**
     * Convert a group of digits in a {@link CharSequence} to an {@code int}.
     *
     * @param   text    the {@link CharSequence}
     * @param   start   the start offset of the digits
     * @param   end     the end offset of the digits
     * @return  the result as an {@code int}
     * @throws  IndexOutOfBoundsException if start or end invalid
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for an
     *          {@code int}
     */
    public static int convertToInt(CharSequence text, int start, int end) {
        if (start < 0 || end > text.length() || start >= end)
            throw new IndexOutOfBoundsException();
        int result = 0;
        for (int i = start; i < end; i++) {
            int n = convertDecDigit(text.charAt(i));
            if (result > 214748364 || result == 214748364 && n > 7)
                throw new NumberFormatException();
            result = result * 10 + n;
        }
        return result;
    }

    /**
     * Convert a group of digits in a {@link CharSequence} to a {@code long}.
     *
     * @param   text    the {@link CharSequence}
     * @param   start   the start offset of the digits
     * @param   end     the end offset of the digits
     * @return  the result as a {@code long}
     * @throws  IndexOutOfBoundsException if start or end invalid
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for a
     *          {@code long}
     */
    public static long convertToLong(CharSequence text, int start, int end) {
        if (start < 0 || end > text.length() || start >= end)
            throw new IndexOutOfBoundsException();
        long result = 0;
        for (int i = start; i < end; i++) {
            int n = convertDecDigit(text.charAt(i));
            if (result > 922337203685477580L || result == 922337203685477580L && n > 7)
                throw new NumberFormatException();
            result = result * 10 + n;
        }
        return result;
    }

    /**
     * Convert a decimal digit to the integer value of the digit.
     *
     * @param ch    the decimal digit
     * @return      the integer value (0 - 9)
     * @throws      NumberFormatException if the digit is not valid
     */
    public static int convertDecDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        throw new NumberFormatException();
    }

    /**
     * Convert a group of hexadecimal digits in a {@link CharSequence} to an {@code int}.
     *
     * @param   text    the {@link CharSequence}
     * @param   start   the start offset of the digits
     * @param   end     the end offset of the digits
     * @return  the result as an {@code int}
     * @throws  IndexOutOfBoundsException if start or end invalid
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for an
     *          {@code int}
     */
    public static int convertHexToInt(CharSequence text, int start, int end) {
        if (start < 0 || end > text.length() || start >= end)
            throw new IndexOutOfBoundsException();
        int result = 0;
        for (int i = start; i < end; i++) {
            if ((result & 0xF0000000) != 0)
                throw new NumberFormatException();
            result = result << 4 | convertHexDigit(text.charAt(i));
        }
        return result;
    }

    /**
     * Convert a group of hexadecimal digits in a {@link CharSequence} to a {@code long}.
     *
     * @param   text    the {@link CharSequence}
     * @param   start   the start offset of the digits
     * @param   end     the end offset of the digits
     * @return  the result as a {@code long}
     * @throws  IndexOutOfBoundsException if start or end invalid
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for a
     *          {@code long}
     */
    public static long convertHexToLong(CharSequence text, int start, int end) {
        if (start < 0 || end > text.length() || start >= end)
            throw new IndexOutOfBoundsException();
        long result = 0;
        for (int i = start; i < end; i++) {
            if ((result & 0xF000000000000000L) != 0)
                throw new NumberFormatException();
            result = result << 4 | convertHexDigit(text.charAt(i));
        }
        return result;
    }

    /**
     * Convert a hexadecimal digit to the integer value of the digit.
     *
     * @param ch    the hexadecimal digit
     * @return      the integer value (0 - 15)
     * @throws      NumberFormatException if the digit is not valid
     */
    public static int convertHexDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        throw new NumberFormatException();
    }

    public static boolean multiWildcardCompare(String pattern, CharSequence target) {
        int patIndex = 0;
        for (;;) {
            int i = pattern.indexOf('|', patIndex);
            if (i < 0)
                break;
            if (wildcardCompare(pattern, patIndex, i, target))
                return true;
            patIndex = i + 1;
        }
        return wildcardCompare(pattern, patIndex, pattern.length(), target);
    }

    /**
     * Perform wildcard comparison.  The method returns {@code true} if the target string
     * matches the pattern, according to the common wildcard rules:
     * <dl style="padding-left:4em">
     *   <dt>&ldquo;<code>*</code>&rdquo;</dt>
     *   <dd>matches zero or more variable characters</dd>
     *   <dt>&ldquo;<code>?</code>&rdquo;</dt>
     *   <dd>matches a single variable character</dd>
     *   <dt>anything else</dt>
     *   <dd>matches the same character exactly</dd>
     * </dl>
     *
     * @param pattern  the pattern string, which may include wildcard characters as described
     *                 above
     * @param target   the target {@link CharSequence} ({@link String}, {@link StringBuilder},
     *                 {@link StringBuffer} etc.)
     * @return         {@code true} if the target string matches the pattern
     */
    public static boolean wildcardCompare(String pattern, CharSequence target) {
//        int tarLen = target.length();
//        int patLen = pattern.length();
//        int i = pattern.indexOf('*');
//        if (i < 0)
//            return tarLen == patLen && wildcardCompareSubstring(pattern, 0, tarLen, target, 0);
//        if (i > tarLen || !wildcardCompareSubstring(pattern, 0, i, target, 0))
//            return false;
//        int tarIndex = i;
//        int patIndex = i + 1;
//        for (;;) {
//            i = pattern.indexOf('*', patIndex);
//            if (i < 0)
//                break;
//            i -= patIndex; // i is now length of substring before next *
//            for (;;) {
//                if (tarIndex + i > tarLen)
//                    return false;
//                if (wildcardCompareSubstring(pattern, patIndex, patIndex + i, target, tarIndex))
//                    break;
//                tarIndex++;
//            }
//            tarIndex += i;
//            patIndex += i + 1;
//        }
//        i = tarLen - (patLen - patIndex); // offset within target
//        return tarIndex <= i && wildcardCompareSubstring(pattern, patIndex, patLen, target, i);
        return wildcardCompare(pattern, 0, pattern.length(), target);
    }

    /**
     * Perform wildcard comparison.  The method returns {@code true} if the target string
     * matches the pattern, according to the common wildcard rules:
     * <dl style="padding-left:4em">
     *   <dt>&ldquo;<code>*</code>&rdquo;</dt>
     *   <dd>matches zero or more variable characters</dd>
     *   <dt>&ldquo;<code>?</code>&rdquo;</dt>
     *   <dd>matches a single variable character</dd>
     *   <dt>anything else</dt>
     *   <dd>matches the same character exactly</dd>
     * </dl>
     * This version allows a substring of the pattern string to be specified, removing the
     * necessity for a separate {@link String#substring(int, int)} operation.
     *
     * @param pattern  the pattern string, which may include wildcard characters as described
     *                 above
     * @param patIndex the start index within the pattern string
     * @param patEnd   the end index within the pattern string
     * @param target   the target {@link CharSequence} ({@link String}, {@link StringBuilder},
     *                 {@link StringBuffer} etc.)
     * @return         {@code true} if the target string matches the pattern
     */
    public static boolean wildcardCompare(String pattern, int patIndex, int patEnd,
            CharSequence target) {
        int tarLen = target.length();
        int i = pattern.indexOf('*', patIndex);
        if (i < 0 || i >= patEnd)
            return tarLen == patEnd - patIndex &&
                    wildcardCompareSubstring(pattern, patIndex, patEnd, target, 0);
        if (i - patIndex > tarLen || !wildcardCompareSubstring(pattern, patIndex, i, target, 0))
            return false;
        int tarIndex = i - patIndex;
        patIndex = i + 1;
        for (;;) {
            i = pattern.indexOf('*', patIndex);
            if (i < 0 || i >= patEnd)
                break;
            i -= patIndex; // i is now length of substring before next *
            for (;;) {
                if (tarIndex + i > tarLen)
                    return false;
                if (wildcardCompareSubstring(pattern, patIndex, patIndex + i, target, tarIndex))
                    break;
                tarIndex++;
            }
            tarIndex += i;
            patIndex += i + 1;
        }
        i = tarLen - (patEnd - patIndex); // offset within str
        return tarIndex <= i && wildcardCompareSubstring(pattern, patIndex, patEnd, target, i);
    }

    /**
     * Compare a substring of a wildcard match.  The substring may contain <code>?</code> but
     * not <code>*</code> characters, so the pattern and target substrings must be the same
     * length.
     *
     * @param pattern   the pattern string
     * @param patIndex  the index of the substring within the pattern string
     * @param patEnd    the end index of the substring within the pattern string
     * @param target    the target {@link CharSequence} ({@link String}, {@link StringBuilder},
     *                  {@link StringBuffer} etc.)
     * @param index     the index of the substring within the target
     * @return          {@code true} if the substrings match
     */
    private static boolean wildcardCompareSubstring(String pattern, int patIndex, int patEnd,
            CharSequence target, int index) {
        while (patIndex < patEnd) {
            char ch = pattern.charAt(patIndex++);
            // don't re-order the comparison below - the auto-increment must always be done
            if (target.charAt(index++) != ch && ch != '?')
                return false;
        }
        return true;
    }

}
