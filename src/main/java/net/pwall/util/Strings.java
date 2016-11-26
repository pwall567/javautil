/*
 * @(#) Strings.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014, 2015 Peter Wall
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
import java.util.Objects;

/**
 * String utility functions.
 *
 * @author Peter Wall
 */
public class Strings {

    private static final String[] numberNamesEnglish = { "zero", "one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen",
            "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };
    private static final String[] tensNamesEnglish = { "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety" };

    private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F' };

    private static final String emptyString = "";
    private static final String[] emptyStringArray = {};

    /**
     * Private constructor to prevent instantiation.  Attempts to instantiate the class via
     * reflection will cause an {@link IllegalAccessException}.
     *
     * @throws  IllegalAccessException in all cases
     */
    private Strings() throws IllegalAccessException {
        throw new IllegalAccessException("Attempt to instantiate Strings class");
    }

    /**
     * Convert a number to words in English.
     *
     * @param   n       the number
     * @return          a string containing the number in words
     */
    public static String toEnglish(int n) {
        if (n >= 0 && n < 20)
            return numberNamesEnglish[n]; // avoids allocating StringBuilder
        StringBuilder sb = new StringBuilder();
        try {
            appendEnglish(sb, n);
        }
        catch (IOException e) {
            // can't happen - StringBuilder does not throw IOException
        }
        return sb.toString();
    }

    /**
     * Append a number converted to words in English to an {@link Appendable}.
     *
     * @param   a   the {@link Appendable}
     * @param   n   the number
     * @return  the {@link Appendable} (for chaining)
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static Appendable appendEnglish(Appendable a, int n) throws IOException {
        if (n >= 0 && n < 20) // optimisation also handles zero
            return a.append(numberNamesEnglish[n]);
        if (n < 0) {
            a.append("minus ");
            if (n == Integer.MIN_VALUE) { // can't simply negate MIN_VALUE
                a.append("two billion, ");
                n = (int)(0x80000000L % 1_000_000_000);
            }
            else
                n = -n;
        }
    concat: {
            if (n >= 1_000_000_000) { // signed 32-bit int can't be greater than three billion
                appendEnglish(a, n / 1_000_000_000).append(" billion");
                if ((n %= 1_000_000_000) == 0)
                    break concat;
                a.append(n >= 100 ? ", " : " and ");
            }
            if (n >= 1_000_000) {
                appendEnglish(a, n / 1_000_000).append(" million");
                if ((n %= 1_000_000) == 0)
                    break concat;
                a.append(n >= 100 ? ", " : " and ");
            }
            if (n >= 1_000) {
                appendEnglish(a, n / 1_000).append(" thousand");
                if ((n %= 1_000) == 0)
                    break concat;
                a.append(n >= 100 ? ", " : " and ");
            }
            if (n >= 100) {
                a.append(numberNamesEnglish[n / 100]).append(" hundred");
                if ((n %= 100) == 0)
                    break concat;
                a.append(" and ");
            }
            if (n >= 20) {
                a.append(tensNamesEnglish[n / 10 - 1]);
                if ((n %= 10) != 0)
                    a.append('-');
            }
            if (n > 0)
                a.append(numberNamesEnglish[n]);
        }
        return a;
    }

    /**
     * Capitalise the first letter of a string.  If the first character is not a lower-case
     * letter the string is returned unmodified.
     *
     * @param   str     the input string
     * @return          the string, with the first letter capitalised
     */
    public static String capitalise(String str) {
        int n = str.length();
        if (n > 0) {
            char ch = str.charAt(0);
            if (Character.isLowerCase(ch)) {
                StringBuilder sb = new StringBuilder(n);
                sb.append(Character.toUpperCase(ch));
                if (n > 1)
                    sb.append(str, 1, n);
                return sb.toString();
            }
        }
        return str;
    }

    /**
     * Create a string consisting of a number, space, and the singular or plural form of a given
     * noun (using standard English plural forms, i.e.&nbsp;add the letter "s").  For example:
     * <pre>
     *     Strings.plural("file", 23);
     * </pre>
     * will return:
     * <pre>
     *     "23 files"
     * </pre>
     *
     * @param   noun    the noun
     * @param   n       the number
     * @return  the string
     */
    public static String plural(String noun, int n) {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append(' ').append(noun);
        if (n != 1)
            sb.append('s');
        return sb.toString();
    }

    /**
     * Create a string consisting of a number, space, and the singular or plural noun (for use
     * with irregular plurals).  For example:
     * <pre>
     *     Strings.plural("axis", "axes", 2);
     * </pre>
     * will return:
     * <pre>
     *     "2 axes"
     * </pre>
     *
     * @param   singularNoun    the singular noun
     * @param   pluralNoun      the plural noun
     * @param   n               the number
     * @return  the string
     */
    public static String plural(String singularNoun, String pluralNoun, int n) {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append(' ').append(n == 1 ? singularNoun : pluralNoun);
        return sb.toString();
    }

    /**
     * Split a string into white space delimited tokens, where white space is determined by
     * {@link Character#isWhitespace(char)}.
     *
     * @param   s       the string to be split
     * @return          an array of tokens (possibly empty)
     * @throws          NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s) {
        return split(s, 0, s.length(), defaultSpaceTest);
    }

    /**
     * Split a portion of a string into white space delimited tokens, where white space is
     * determined by {@link Character#isWhitespace(char)}.
     *
     * @param   s       the string to be split
     * @param   start   the start index of the portion to be examined
     * @param   end     the end index (exclusive) of the portion to be examined
     * @return          an array of tokens (possibly empty)
     * @throws          NullPointerException if the input string is {@code null}
     * @throws          IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end) {
        return split(s, start, end, defaultSpaceTest);
    }

    /**
     * Split a string into white space delimited tokens, where white space is determined by a
     * supplied {@link SpaceTest} object.  In Java 8 lambda notation, this may be called by:
     * <pre>
     *     String[] array = Strings.split("a b c d e", ch -&gt; Character.isSpace(ch));
     * </pre>
     *
     * @param   s       the string to be split
     * @param   st      the {@link SpaceTest}
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
     * @param   st      the {@link SpaceTest}
     * @return          an array of tokens (possibly empty)
     * @throws          NullPointerException if the input string is {@code null}
     * @throws          IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end, SpaceTest st) {
        // first, trim spaces from the start of the string; if we hit end, return empty array
        for (;;) {
            if (start >= end)
                return emptyStringArray;
            if (!st.isSpace(s.charAt(start)))
                break;
            start++;
        }
        // now trim spaces from end (by this stage, we know there's at least one non-space)
        while (st.isSpace(s.charAt(--end)))
            ;
        // first pass through the string to count the number of internal groups of spaces
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
        // result array size is number of separators plus 1
        String[] result = new String[count + 1];
        // for each result entry prior to the last...
        i = start;
        for (int j = 0; j < count; j++) {
            // store start index and skip past non-space
            int k = i;
            do {
                i++;
            } while (!st.isSpace(s.charAt(i)));
            // add result substring to array
            result[j] = s.substring(k, i);
            // and skip past spaces
            do {
                i++;
            } while (st.isSpace(s.charAt(i)));
        }
        // the last entry consists of the remainder of the (trimmed) string
        result[count] = s.substring(i, end + 1);
        return result;
    }

    /**
     * Split a string on a given string separator.
     *
     * @param   s1      the string to be split
     * @param   s2      the separator
     * @return          an array of items (possibly empty)
     * @throws          NullPointerException if either string is {@code null}
     */
    public static String[] split(String s1, String s2) {
        // first pass through the string to count the number of separators
        int count = 0;
        int i = 0;
        int n2 = s2.length();
        int stopper = s1.length() - n2;
        while (i <= stopper) {
            if (s1.regionMatches(i, s2, 0, n2)) {
                count++;
                i += n2;
            }
            else
                i++;
        }
        // result array size is number of separators plus 1
        String[] result = new String[count + 1];
        // for each result entry prior to the last...
        i = 0;
        for (int j = 0; j < count; j++) {
            // store start index and skip to separator
            int k = i;
            while (!s1.regionMatches(i, s2, 0, n2))
                i++;
            // add result substring to array
            result[j] = s1.substring(k, i);
            // and skip past separator
            i += n2;
        }
        // the last entry consists of the remainder of the string
        result[count] = s1.substring(i);
        return result;
    }

    /**
     * An interface to define a space test for the associated {@code split()} functions.  The
     * interface is named {@code SpaceTest} rather than {@code SeparatorTest} because the
     * functions that use it treat multiple occurrences as equivalent to a single character -
     * this is generally the required behaviour for spaces but not for, say, commas.
     *
     * @see     Strings#split(String, SpaceTest)
     * @see     Strings#split(String, int, int, SpaceTest)
     */
    public static interface SpaceTest {
        boolean isSpace(int ch);
    }

    public static final SpaceTest defaultSpaceTest = new SpaceTest() {
        @Override
        public boolean isSpace(int ch) {
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
        return split(s, 0, s.length(), separator, true, defaultSpaceTest);
    }

    /**
     * Split a string on a given separator character.  Spaces may optionally be trimmed from
     * both ends of the items using the supplied space test, and zero-length items (after
     * optional trimming) may optionally be dropped.
     *
     * @param   s           the string to be split
     * @param   separator   the separator
     * @param   skipEmpty   if {@code true}, ignore zero-length items (possibly after trimming)
     * @param   spaceTest   if not {@code null}, use to trim spaces off both ends of each item
     * @return              an array of items (possibly empty)
     * @throws              NullPointerException if the input string is {@code null}
     */
    public static String[] split(String s, char separator, boolean skipEmpty,
            SpaceTest spaceTest) {
        return split(s, 0, s.length(), separator, skipEmpty, spaceTest);
    }

    /**
     * Split a portion of a string on a given separator character.  Spaces may optionally be
     * trimmed from both ends of the items using the supplied space test, and zero-length items
     * (after optional trimming) may optionally be dropped.
     *
     * @param   s           the string to be split
     * @param   start       the start index of the portion to be examined
     * @param   end         the end index (exclusive) of the portion to be examined
     * @param   separator   the separator
     * @param   skipEmpty   if {@code true}, ignore zero-length items (possibly after trimming)
     * @param   spaceTest   if not {@code null}, use to trim spaces off both ends of each item
     * @return              an array of items (possibly empty)
     * @throws              NullPointerException if the input string is {@code null}
     * @throws              IndexOutOfBoundsException if {@code start} or {@code end} is invalid
     */
    public static String[] split(String s, int start, int end, char separator,
            boolean skipEmpty, SpaceTest spaceTest) {
        int count = 0;
        int i = start;
        if (skipEmpty) {
            if (spaceTest != null) {
                // count the number of items (ignoring zero-length or all space items)
                for (;;) {
                    boolean nonSpaceSeen = false;
                    while (i < end) {
                        char ch = s.charAt(i);
                        if (ch == separator)
                            break;
                        nonSpaceSeen = nonSpaceSeen || !spaceTest.isSpace(ch);
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
            if (spaceTest != null) {
                while (itemStart < itemEnd && spaceTest.isSpace(s.charAt(itemStart)))
                    itemStart++;
                while (itemStart < itemEnd && spaceTest.isSpace(s.charAt(itemEnd - 1)))
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
     * @param   <E>         class of collection item
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
     * @param   <E>     class of collection item
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
     * @param   <E>     class of collection item
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
     * @param   <E>     class of array item
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
     * @param   <E>         class of collection item
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
     * @param   <E>         class of collection item
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
     * @param   <E>         class of collection item
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
     * @param   <E>         class of array item
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
     * @param   <E>         class of collection item
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
     * @param   <E>         class of collection item
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
     * @param   <E>         class of collection item
     * @param   e           the {@link Enumeration}
     * @param   separator   the separator
     * @return  the concatenation of the string representations of the members (an empty string
     *          if the {@link Enumeration} has no members)
     */
    public static <E> String join(Enumeration<E> e, String separator) {
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
     * Join the string representations of the members of an array, with the specified string
     * separator.
     *
     * @param   <E>         class of array item
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
            String mapped = mapper.map(s.charAt(i++));
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
            String mapped = mapper.map(s.charAt(i++));
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
     * Convert a byte array from UTF-8 encoding to a UTF-16 string.
     *
     * @param   bytes   the byte array
     * @return          the decoded string
     * @throws          IllegalArgumentException if the byte array is {@code null}, or if the
     *                  byte array contains an invalid UTF-8 sequence
     */
    public static String fromUTF8(byte[] bytes) {
        if (bytes == null)
            throw new IllegalArgumentException("Byte array must not be null");
        return fromUTF8(bytes, 0, bytes.length);
    }

    /**
     * Convert a portion of a string to UTF-8 encoding.
     *
     * @param   str     the string
     * @param   start   the start index
     * @param   end     the end index
     * @return          the UTF-8 form of the string (as a byte array)
     * @throws          IllegalArgumentException if the string is {@code null}, or if the string
     *                  contains an invalid UTF-16 sequence
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
            appendUTF8(bab, codePoint);
        }
        return bab.toByteArray();
    }

    public static void appendUTF8(ByteArrayBuilder bab, int codePoint) {
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

    /**
     * Convert a portion of a byte array from UTF-8 encoding to a UTF-16 string.
     *
     * @param   bytes   the byte array
     * @param   start   the start index
     * @param   end     the end index
     * @return          the decoded string
     * @throws          IllegalArgumentException if the byte array is {@code null}, if the start
     *                  or end index is invalid, or if the byte array contains an invalid UTF-8
     *                  sequence
     */
    public static String fromUTF8(byte[] bytes, int start, int end) {
        if (bytes == null)
            throw new IllegalArgumentException("Byte array must not be null");
        if (start < 0 || start > bytes.length)
            throw new IllegalArgumentException("Start index invalid: " + start);
        if (end < start || end > bytes.length)
            throw new IllegalArgumentException("End index invalid: " + end);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            int b = bytes[i];
            if ((b & 0x80) == 0)
                sb.append((char)b);
            else if ((b & 0x40) == 0)
                throw new IllegalArgumentException("Illegal character in UTF-8 bytes");
            else if ((b & 0x20) == 0) {
                int codePoint = b & 0x1F;
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                sb.append((char)codePoint);
            }
            else if ((b & 0x10) == 0) {
                int codePoint = b & 0x0F;
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                sb.append((char)codePoint);
            }
            else {
                int codePoint = b & 0x07;
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                codePoint = addToCodePoint(codePoint, bytes, ++i, end);
                try {
                    appendUTF16(sb, codePoint);
                }
                catch (IOException ioe) {
                    // can't happen - StringBuilder.append() does not throw IOException
                }
            }
        }
        return sb.toString();
    }

    /**
     * Accumulate codepoint (UTF-8 decoding).
     *
     * @param   codePoint   the codepoint so far
     * @param   bytes       the byte array
     * @param   index       the current index into the array
     * @param   end         the end index
     * @return              the updated codepoint
     * @throws  IllegalArgumentException if the bytes are invalid
     */
    private static int addToCodePoint(int codePoint, byte[] bytes, int index, int end) {
        if (index >= end)
            throw new IllegalArgumentException("Incomplete sequence in UTF-8 bytes");
        int b = bytes[index];
        if ((b & 0xC0) != 0x80)
            throw new IllegalArgumentException("Illegal character in UTF-8 bytes");
        return (codePoint << 6) | (b & 0x3F);
    }

    /**
     * Convert a {@link CharSequence} to hexadecimal.
     *
     * @param   s       the {@link CharSequence}
     * @return          the converted string
     * @throws          IllegalArgumentException if the {@link CharSequence} is {@code null}
     */
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

    /**
     * Convert a {@link CharSequence} to hexadecimal, with a separator between bytes for easier
     * reading.
     *
     * @param   s           the {@link CharSequence}
     * @param   separator   the separator
     * @return              the converted string
     * @throws              IllegalArgumentException if the {@link CharSequence} is {@code null}
     */
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

    /**
     * Convert a byte array to hexadecimal.
     *
     * @param   bytes   the byte array
     * @return          the converted string
     * @throws          IllegalArgumentException if the byte array is {@code null}
     */
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

    /**
     * Convert a byte array to hexadecimal, with a separator between bytes for easier reading.
     *
     * @param   bytes       the byte array
     * @param   separator   the separator
     * @return              the converted string
     * @throws              IllegalArgumentException if the byte array is {@code null}
     */
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

    /**
     * Convert a byte to hexadecimal.
     *
     * @param   b       the byte
     * @return          the converted string
     */
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

    /**
     * Convert a character to hexadecimal.
     *
     * @param   ch      the character
     * @return          the converted string
     */
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

    /**
     * Convert an integer to hexadecimal.
     *
     * @param   i       the integer
     * @return          the converted string
     */
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

    /**
     * Convert a long to hexadecimal.
     *
     * @param   n       the number
     * @return          the converted string
     */
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

    /**
     * Append a byte value as hexadecimal to an {@link Appendable}.
     *
     * @param   a       the {@link Appendable}
     * @param   b       the byte
     * @throws IOException if thrown by the {@link Appendable}
     */
    public static void appendHex(Appendable a, byte b) throws IOException {
        a.append(hexDigits[(b >>> 4) & 0xF]);
        a.append(hexDigits[b & 0xF]);
    }

    /**
     * Append a character value as hexadecimal to an {@link Appendable}.
     *
     * @param   a       the {@link Appendable}
     * @param   ch      the character
     * @throws IOException if thrown by the {@link Appendable}
     */
    public static void appendHex(Appendable a, char ch) throws IOException {
        appendHex(a, (byte)(ch >>> 8));
        appendHex(a, (byte)ch);
    }

    /**
     * Append an integer value as hexadecimal to an {@link Appendable}.
     *
     * @param   a       the {@link Appendable}
     * @param   i       the number
     * @throws IOException if thrown by the {@link Appendable}
     */
    public static void appendHex(Appendable a, int i) throws IOException {
        appendHex(a, (char)(i >>> 16));
        appendHex(a, (char)i);
    }

    /**
     * Append a long value as hexadecimal to an {@link Appendable}.
     *
     * @param   a       the {@link Appendable}
     * @param   n       the number
     * @throws IOException if thrown by the {@link Appendable}
     */
    public static void appendHex(Appendable a, long n) throws IOException {
        appendHex(a, (int)(n >>> 32));
        appendHex(a, (int)n);
    }

    private static final int MAX_INT_DIV_10 = Integer.MAX_VALUE / 10;
    private static final int MAX_INT_MOD_10 = Integer.MAX_VALUE % 10;

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
            if (result > MAX_INT_DIV_10 || result == MAX_INT_DIV_10 && n > MAX_INT_MOD_10)
                throw new NumberFormatException();
            result = result * 10 + n;
        }
        return result;
    }

    private static final long MAX_LONG_DIV_10 = Long.MAX_VALUE / 10;
    private static final int MAX_LONG_MOD_10 = (int)(Long.MAX_VALUE % 10);

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
            if (result > MAX_LONG_DIV_10 || result == MAX_LONG_DIV_10 && n > MAX_LONG_MOD_10)
                throw new NumberFormatException();
            result = result * 10 + n;
        }
        return result;
    }

    /**
     * Convert a decimal digit to the integer value of the digit.
     *
     * @param   ch      the decimal digit
     * @return          the integer value (0 - 9)
     * @throws          NumberFormatException if the digit is not valid
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
            if ((result & 0xF8000000) != 0)
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
            if ((result & 0xF800000000000000L) != 0)
                throw new NumberFormatException();
            result = result << 4 | convertHexDigit(text.charAt(i));
        }
        return result;
    }

    /**
     * Convert a hexadecimal digit to the integer value of the digit.
     *
     * @param   ch      the hexadecimal digit
     * @return          the integer value (0 - 15)
     * @throws          NumberFormatException if the digit is not valid
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

    /**
     * Perform multi-wildcard comparison.  The pattern string consists of multiple wildcard
     * patterns (using <code>?</code> for single character matches and <code>*</code> for
     * multiple character matches) separated by vertical bar (logical or) characters.  The
     * comparison returns {@code true} if any of the patterns match the target.
     *
     * @param   pattern the pattern string as described above
     * @param   target  the target {@link CharSequence} ({@link String}, {@link StringBuilder},
     *                  {@link StringBuffer} etc.)
     * @return          {@code true} if the target string matches the pattern
     * @see             #wildcardCompare(String, int, int, CharSequence)
     */
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
     * @param   pattern the pattern string, which may include wildcard characters as described
     *                  above
     * @param   target  the target {@link CharSequence} ({@link String}, {@link StringBuilder},
     *                  {@link StringBuffer} etc.)
     * @return          {@code true} if the target string matches the pattern
     */
    public static boolean wildcardCompare(String pattern, CharSequence target) {
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
     * @param   pattern     the pattern string, which may include wildcard characters as
     *                      described above
     * @param   patIndex    the start index within the pattern string
     * @param   patEnd      the end index within the pattern string
     * @param   target      the target {@link CharSequence} ({@link String},
     *                      {@link StringBuilder}, {@link StringBuffer} etc.)
     * @return              {@code true} if the target string matches the pattern
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
     * @param   pattern     the pattern string
     * @param   patIndex    the index of the substring within the pattern string
     * @param   patEnd      the end index of the substring within the pattern string
     * @param   target      the target {@link CharSequence} ({@link String},
     *                      {@link StringBuilder}, {@link StringBuffer} etc.)
     * @param   index       the index of the substring within the target
     * @return              {@code true} if the substrings match
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

    /**
     * Trim leading and trailing characters from a string, where those characters match a
     * supplied {@link SpaceTest} interface implementation.
     *
     * @param   s           the string to be trimmed
     * @param   spaceTest   the {@link SpaceTest}
     * @return  the trimmed string
     * @throws  NullPointerException if either argument is {@code null}
     */
    public static String trim(String s, SpaceTest spaceTest) {
        Objects.requireNonNull(spaceTest);
        int start = 0;
        int end = s.length();
        for (;;) {
            if (start >= end)
                return emptyString;
            if (!spaceTest.isSpace(s.charAt(start)))
                break;
            start++;
        }
        while (spaceTest.isSpace(s.charAt(end - 1)))
            end--;
        return start == 0 && end == s.length() ? s : s.substring(start, end);
    }

    /**
     * Trim leading and trailing characters from a {@link CharSequence}, where those characters
     * match a supplied {@link SpaceTest} functional interface implementation.
     *
     * @param   cs          the {@link CharSequence} to be trimmed
     * @param   spaceTest   the {@link SpaceTest}
     * @return  the trimmed {@link CharSequence}
     * @throws  NullPointerException if either argument is {@code null}
     */
    public static CharSequence trim(CharSequence cs, SpaceTest spaceTest) {
        Objects.requireNonNull(spaceTest);
        int start = 0;
        int end = cs.length();
        for (;;) {
            if (start >= end)
                return emptyString;
            if (!spaceTest.isSpace(cs.charAt(start)))
                break;
            start++;
        }
        while (spaceTest.isSpace(cs.charAt(end - 1)))
            end--;
        return start == 0 && end == cs.length() ? cs : new SubSequence(cs, start, end);
    }

    /**
     * Trim leading and trailing whitespace from a string, where white space is determined by
     * {@link Character#isWhitespace(char)}.
     *
     * @param   s       the string to be trimmed
     * @return  the trimmed string
     * @throws  NullPointerException if the input string is {@code null}
     */
    public static String trim(String s) {
        return trim(s, defaultSpaceTest);
    }

    /**
     * Trim leading and trailing whitespace from a {@link CharSequence}, where white space is
     * determined by {@link Character#isWhitespace(char)}.
     *
     * @param   cs      the {@link CharSequence} to be trimmed
     * @return  the trimmed {@link CharSequence}
     * @throws  NullPointerException if the input {@link CharSequence} is {@code null}
     */
    public static CharSequence trim(CharSequence cs) {
        return trim(cs, defaultSpaceTest);
    }

    /**
     * Trim leading and trailing code points from a UTF16 string, where those code points match
     * a supplied {@link SpaceTest} interface implementation.
     *
     * @param s the string to be trimmed
     * @param spaceTest the {@link SpaceTest}
     * @return the trimmed string
     * @throws NullPointerException if either argument is {@code null}
     */
    public static String trimUTF16(String s, SpaceTest spaceTest) {
        Objects.requireNonNull(spaceTest);
        int start = 0;
        int end = s.length();
        for (;;) {
            if (start >= end)
                return emptyString;
            char hi = s.charAt(start);
            if (Character.isHighSurrogate(hi) && start + 1 < end) {
                char lo = s.charAt(start + 1);
                if (Character.isLowSurrogate(lo)) {
                    if (!spaceTest.isSpace(Character.toCodePoint(hi, lo)))
                        break;
                    start += 2;
                    continue;
                }
            }
            if (!spaceTest.isSpace(hi))
                break;
            start++;
        }
        while (end > start) {
            char lo = s.charAt(end - 1);
            if (Character.isLowSurrogate(lo) && end - 1 > start) {
                char hi = s.charAt(end - 2);
                if (Character.isHighSurrogate(hi)) {
                    if (!spaceTest.isSpace(Character.toCodePoint(hi, lo)))
                        break;
                    end -= 2;
                    continue;
                }
            }
            if (!spaceTest.isSpace(lo))
                break;
            end--;
        }
        return start == 0 && end == s.length() ? s : s.substring(start, end);
    }

    /**
     * Convenience method to create a {@link StringBuilder}.  Supports the idiom:
     * <pre>
     *     String str = Strings.build().append('(').append(n).append(')').toString();
     * </pre>
     *
     * @return  the new {@link StringBuilder}
     */
    public static StringBuilder build() {
        return new StringBuilder();
    }

    /**
     * Convenience method to create a {@link StringBuilder}.  Supports the idiom:
     * <pre>
     *     String str = Strings.build("(").append(n).append(')').toString();
     * </pre>
     *
     * @param   cs      the initial contents
     * @return  the new {@link StringBuilder}
     */
    public static StringBuilder build(CharSequence cs) {
        return new StringBuilder(cs);
    }

    /**
     * Convert an integer to a spreadsheet-style column identifier ("A", "B", ... "Z", "AA"
     * etc.).
     *
     * @param   i       the number to convert
     * @return  the identifier
     */
    public static String toIdentifier(int i) {
        StringBuilder sb = new StringBuilder();
        i = Math.abs(i);
        do {
            sb.insert(0, (char)(i % 26 + 'A'));
            i = i / 26 - 1;
        } while (i >= 0);
        return sb.toString();
    }

    private static char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static char[] tensDigits = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };

    /**
     * Append an {@code int} to an {@link Appendable}.  This method outputs the digits left to
     * right, avoiding the need to allocate a separate buffer.
     *
     * @param   a   the {@link Appendable}
     * @param   i   the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendInt(Appendable a, int i) throws IOException {
        if (i < 0) {
            if (i == Integer.MIN_VALUE) {
                a.append("-2147483648");
                return;
            }
            a.append('-');
            appendPositiveInt(a, -i);
        }
        else
            appendPositiveInt(a, i);
    }

    /**
     * Append a positive {@code int} to an {@link Appendable}.  This method outputs the digits
     * left to right, avoiding the need to allocate a separate buffer.
     *
     * @param   a   the {@link Appendable}
     * @param   i   the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveInt(Appendable a, int i) throws IOException {
        if (i >= 100) {
            int n = i / 100;
            appendPositiveInt(a, n);
            i -= n * 100;
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else if (i >= 10) {
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else
            a.append(digits[i]);
    }

    /**
     * Append a {@code long} to an {@link Appendable}.  This method outputs the digits left to
     * right, avoiding the need to allocate a separate buffer.
     *
     * @param   a   the {@link Appendable}
     * @param   n   the {@code long}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLong(Appendable a, long n) throws IOException {
        if (n < 0) {
            if (n == Long.MIN_VALUE) {
                a.append("-9223372036854775808");
                return;
            }
            a.append('-');
            appendPositiveLong(a, -n);
        }
        else
            appendPositiveLong(a, n);
    }

    /**
     * Append a positive {@code long} to an {@link Appendable}.  This method outputs the digits
     * left to right, avoiding the need to allocate a separate buffer.
     *
     * @param   a   the {@link Appendable}
     * @param   n   the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveLong(Appendable a, long n) throws IOException {
        if (n >= 100) {
            long m = n / 100;
            appendPositiveLong(a, m);
            int i = (int)(n - m * 100);
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else if (n >= 10) {
            a.append(tensDigits[(int)n]);
            a.append(digits[(int)n]);
        }
        else
            a.append(digits[(int)n]);
    }

}
