/*
 * @(#) ParseText.java
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
import java.util.Collection;

/**
 * A class to assist with text parsing.  A {@code ParseText} object contains a string of text to
 * be examined (in the form of a {@link CharSequence}), an index denoting the current parse
 * position and a start index pointing to the start of the last matched or skipped characters.
 *
 * <p>The class includes a number of "{@code match}" operations, which return {@code true} if
 * the text matches the parameter(s) to the operation.  On a successful match, these operations
 * set the start index to the position of the first character matched, and increment the index
 * past the matched characters.  If the match is not successful, the index and start index are
 * not modified.</p>
 *
 * <p>For use following a successful match operation, there are several "{@code getResult}"
 * operations that will extract the matched characters from the text in a variety of forms
 * ({@code int}, {@code long}, {@link String} etc.).</p>
 *
 * @author Peter Wall
 */
public class ParseText {

    private CharSequence text;
    private int index;
    private int start;

    /**
     * Construct a {@code ParseText} object with the given text and initial index value.
     *
     * @param   text    the text
     * @param   index   the initial index
     * @throws  NullPointerException if the text is {@code null}
     * @throws  StringIndexOutOfBoundsException if the index is negative or beyond the end of
     *          the text
     */
    public ParseText(CharSequence text, int index) {
        setText(text, index);
    }

    /**
     * Construct a {@code ParseText} object with the given text and an initial index value 0f 0.
     *
     * @param   text    the text
     * @throws          NullPointerException if the text is {@code null}
     */
    public ParseText(CharSequence text) {
        setText(text, 0);
    }

    /**
     * Set the text and the index within the text.  The start index is set to the same value as
     * the index.
     *
     * @param   text    the text
     * @param   index   the index
     * @return          the {@code ParseText} object (for chaining purposes)
     * @throws  NullPointerException if the text is {@code null}
     * @throws  StringIndexOutOfBoundsException if the index is outside the bounds of the text
     */
    public ParseText setText(CharSequence text, int index) {
        if (text == null)
            throw new NullPointerException("ParseText data invalid");
        this.text = text;
        setIndex(index);
        start = index;
        return this;
    }

    /**
     * Set the text.  The index and start index are set to zero.
     *
     * @param   text    the text
     * @return          the {@code ParseText} object (for chaining purposes)
     * @throws  NullPointerException if the text is {@code null}
     */
    public ParseText setText(CharSequence text) {
        setText(text, 0);
        return this;
    }

    /**
     * Get the entire text from the {@code ParseText} object (as a {@link CharSequence}).
     *
     * @return  the text
     */
    public CharSequence getText() {
        return text;
    }

    /**
     * Get the length of the entire text.
     *
     * @return  the text length
     */
    public int getTextLength() {
        return text.length();
    }

    /**
     * Test whether the {@code ParseText} object is exhausted (the index has reached the end of
     * the text).
     *
     * @return  {@code true} if the index has reached the end of the text
     */
    public boolean isExhausted() {
        return index >= text.length();
    }

    /**
     * Get the current index (the offset within the text).
     *
     * @return  the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index to a specified value.
     *
     * @param   index   the new index
     * @return          the {@code ParseText} object (for chaining purposes)
     * @throws  StringIndexOutOfBoundsException if the index is outside the bounds of the text
     */
    public ParseText setIndex(int index) {
        if (index < 0 || index > text.length())
            throw new StringIndexOutOfBoundsException("ParseText index invalid");
        this.index = index;
        return this;
    }

    /**
     * Indicate successful match.  This is intended to be used by derived classes.
     *
     * @param   i   the index at end of match
     * @return      {@code true} to indicate successful match
     */
    protected boolean matchSuccess(int i) {
        start = index;
        index = i;
        return true;
    }

    /**
     * Get the current character (the character at the index).
     *
     * @return  the current character
     * @throws  StringIndexOutOfBoundsException if the index is at or beyond end of string
     * @deprecated      use {@link #getCodePoint()} instead
     */
    @Deprecated
    public char getChar() {
        if (index >= text.length())
            throw new StringIndexOutOfBoundsException("ParseText exhausted");
        return text.charAt(index);
    }

    /**
     * Get the Unicode code point at the current character index.
     *
     * @return  the code point
     * @throws  StringIndexOutOfBoundsException if the index is at or beyond end of string
     */
    public int getCodePoint() {
        start = index;
        if (index >= text.length())
            throw new StringIndexOutOfBoundsException("ParseText exhausted");
        char ch = text.charAt(index++);
        if (Character.isHighSurrogate(ch) && index < text.length()) {
            char ch2 = text.charAt(index);
            if (Character.isLowSurrogate(ch2)) {
                index++;
                return (Character.toCodePoint(ch, ch2));
            }
        }
        return ch;
    }

    /**
     * Extract a string from the text, bounded by the given start and end offsets.
     *
     * @param   from    the start offset
     * @param   to      the end offset
     * @return  the specified string
     * @throws  IndexOutOfBoundsException if the start and end offsets are invalid
     */
    public String getString(int from, int to) {
        return text.subSequence(from, to).toString();
    }

    /**
     * Get the start index (the index of the start of the last matched sequence).
     *
     * @return  the start index
     */
    public int getStart() {
        return start;
    }

    /**
     * Set the start index (the index of the start of the last matched sequence).  The start
     * index must be less than or equal to the index (current position).
     *
     * @param   start   the new start index
     * @return          the {@code ParseText} object (for chaining purposes)
     * @throws          StringIndexOutOfBoundsException if the start index is invalid
     */
    public ParseText setStart(int start) {
        if (start < 0 || start > index)
            throw new StringIndexOutOfBoundsException("ParseText start index invalid");
        this.start = start;
        return this;
    }

    private static final int MAX_INT_DIV_10 = Integer.MAX_VALUE / 10;
    private static final int MAX_INT_MOD_10 = Integer.MAX_VALUE % 10;

    /**
     * Get the result of the last match operation as an {@code int}.
     *
     * @return  the result of the last match as an {@code int}
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for an
     *          {@code int}
     */
    public int getResultInt() {
        int result = 0;
        for (int i = start; i < index; i++) {
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
     * Get the result of the last match operation as a {@code long}.
     *
     * @return  the result of the last match as a {@code long}
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for a
     *          {@code long}
     */
    public long getResultLong() {
        long result = 0;
        for (int i = start; i < index; i++) {
            int n = convertDecDigit(text.charAt(i));
            if (result > MAX_LONG_DIV_10 || result == MAX_LONG_DIV_10 && n > MAX_LONG_MOD_10)
                throw new NumberFormatException();
            result = result * 10 + n;
        }
        return result;
    }

    /**
     * Convert a decimal digit to the integer value of the digit.  This method may be overridden
     * to provide for different definitions of a decimal digit.  If this method is overridden it
     * may be necessary to override {@link #isDigit(char)} as well.
     *
     * @param ch    the decimal digit
     * @return      the integer value (0 - 9)
     * @throws      NumberFormatException if the digit is not valid
     */
    public int convertDecDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        throw new NumberFormatException();
    }

    /**
     * Get the result of the last match operation as an {@code int}, treating the digits as
     * hexadecimal.
     *
     * @return  the result of the last match as an {@code int}
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for an
     *          {@code int}
     */
    public int getResultHexInt() {
        int result = 0;
        for (int i = start; i < index; i++) {
            if ((result & 0xF8000000) != 0)
                throw new NumberFormatException();
            result = result << 4 | convertHexDigit(text.charAt(i));
        }
        return result;
    }

    /**
     * Get the result of the last match operation as a {@code long}, treating the digits as
     * hexadecimal.
     *
     * @return  the result of the last match as a {@code long}
     * @throws  NumberFormatException if any digit is invalid, or if the value is too big for a
     *          {@code long}
     */
    public long getResultHexLong() {
        long result = 0;
        for (int i = start; i < index; i++) {
            if ((result & 0xF800000000000000L) != 0)
                throw new NumberFormatException();
            result = result << 4 | convertHexDigit(text.charAt(i));
        }
        return result;
    }

    /**
     * Convert a hexadecimal digit to the integer value of the digit.  This method may be
     * overridden to provide for different definitions of a hexadecimal digit.  If this method
     * is overridden it may be necessary to override {@link #isHexDigit(char)} as well.
     *
     * @param   ch      the hexadecimal digit
     * @return          the integer value (0 - 15)
     * @throws          NumberFormatException if the digit is not valid
     */
    public int convertHexDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        throw new NumberFormatException();
    }

    /**
     * Get the length of the result of the last match operation.
     *
     * @return  the length of the result of the last match
     */
    public int getResultLength() {
        return index - start;
    }

    /**
     * Get the result of the last match operation (or the first character of a longer match) as
     * a single character.
     *
     * @return  the first character of the result of the last match
     */
    public char getResultChar() {
        return text.charAt(start);
    }

    /**
     * Get the result of the last match operation as a {@link CharSequence}.
     *
     * @return  the result of the last match
     */
    public CharSequence getResultSequence() {
//        return seq.subSequence(start, index);
        return new SubSequence(text, start, index);
    }

    /**
     * Get the result of the last match operation as a {@link String}.
     *
     * @return  the result of the last match
     */
    public String getResultString() {
        return text.subSequence(start, index).toString();
    }

    /**
     * Copy the result character sequence to a {@link StringBuilder}.  This is equivalent to but
     * more efficient than
     * <pre>
     *     sb.append(<i>xxx</i>.getResultSequence());
     * </pre>
     * because it avoids the creation of an intermediate object.
     *
     * @param   sb      the {@link StringBuilder}
     * @return          the {@link StringBuilder} (for chaining purposes)
     */
    public StringBuilder appendResultTo(StringBuilder sb) {
        return sb.append(text, start, index);
    }

    /**
     * Copy the result character sequence to an {@link Appendable}.  This is equivalent to but
     * more efficient than
     * <pre>
     *     a.append(<i>xxx</i>.getResultSequence());
     * </pre>
     * because it avoids the creation of an intermediate object.
     *
     * @param   a       the {@link Appendable}
     * @return          the {@link Appendable} (for chaining purposes)
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public Appendable appendResultTo(Appendable a) throws IOException {
        return a.append(text, start, index);
    }

    /**
     * Test whether the text has at least the specified number of characters left after the
     * index.
     *
     * @param   len     the number of characters required
     * @return  {@code true} if that number of characters are available
     */
    public boolean available(int len) {
        return index + len <= text.length();
    }

    /**
     * Get the length of the entire text.
     *
     * @return  the length of the text
     */
    public int length() {
        return text.length();
    }

    /**
     * Get a specific character from the text.
     *
     * @param   index   the index of the character
     * @return  the character at the specified index
     * @throws  IndexOutOfBoundsException if the index is negative or beyond the end of the text
     */
    public char charAt(int index) {
        return text.charAt(index);
    }

    /**
     * Match the current character in the text against a given Unicode code point.  Following a
     * successful match the start index will point to the matched code point and the index will
     * be incremented past it.
     *
     * @param   cp      the code point to match against
     * @return  {@code true} if the code point in the text matches the given character
     */
    public boolean match(int cp) {
        int i = index;
        if (i >= text.length())
            return false;
        char ch = text.charAt(i++);
        if (Character.isHighSurrogate(ch)) {
            if (i >= text.length())
                return false;
            char ch2 = text.charAt(i++);
            if (!Character.isLowSurrogate(ch2))
                return false;
            if (Character.toCodePoint(ch, ch2) != cp)
                return false;
        }
        else
            if (ch != cp)
                return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the current character in the text against a given character.  Following a
     * successful match the start index will point to the matched character and the index will
     * be incremented past it.
     *
     * @param   ch      the character to match against
     * @return  {@code true} if the character in the text matches the given character
     */
    public boolean match(char ch) {
        if (index >= text.length() || text.charAt(index) != ch)
            return false;
        start = index++;
        return true;
    }

    /**
     * Match the current character in the text against a given character, ignoring case.
     * Following a successful match the start index will point to the matched character and the
     * index will be incremented past it.
     *
     * @param ch  the character to match against
     * @return    {@code true} if the character in the text matches the given character
     */
    public boolean matchIgnoreCase(char ch) {
        if (index >= text.length() || !equalIgnoreCase(text.charAt(index), ch))
            return false;
        start = index++;
        return true;
    }

    /**
     * Test characters for equality, ignoring case.
     *
     * @param   a       the first character
     * @param   b       the second character
     * @return          {@code true} if the characters are equal, ignoring case
     */
    private static boolean equalIgnoreCase(char a, char b) {
        return a == b ||
                a == (Character.isLowerCase(a) ? Character.toLowerCase(b) :
                        Character.toUpperCase(b));
    }

    /**
     * Match the current character in the text against a given character range.  Following a
     * successful match the start index will point to the matched character and the index will
     * be incremented past it.
     *
     * @param from the low character in the range to match against
     * @param to   the high character in the range to match against (inclusive)
     * @return     {@code true} if the character in the text falls in the given range
     */
    public boolean matchRange(char from, char to) {
        if (index >= text.length())
            return false;
        char ch = text.charAt(index);
        if (ch < from || ch > to)
            return false;
        start = index++;
        return true;
    }

    /**
     * Match the current character in the text against any of the characters in a given
     * {@link String}.  Following a successful match the start index will point to the matched
     * character and the index will be incremented past it.
     *
     * @param str   the characters to match against (as a {@link String})
     * @return      {@code true} if the character in the text matches any of the characters in
     *              the string
     */
    public boolean matchAnyOf(String str) {
        if (index >= text.length())
            return false;
        if (str.indexOf(text.charAt(index)) < 0)
            return false;
        start = index++;
        return true;
    }

    /**
     * Match the current character in the text against any of the characters in a given array.
     * Following a successful match the start index will point to the matched character and the
     * index will be incremented past it.
     *
     * @param array the characters to match against (as a array)
     * @return      {@code true} if the character in the text matches any of the characters in
     *              the array
     */
    public boolean matchAnyOf(char[] array) {
        if (index >= text.length())
            return false;
        char ch = text.charAt(index);
        for (int i = 0, n = array.length; i < n; i++) {
            if (ch == array[i]) {
                start = index++;
                return true;
            }
        }
        return false;
    }

    /**
     * Match the characters at the index against a given {@link CharSequence} ({@link String},
     * {@link StringBuilder} etc.).  Following a successful match the start index will point to
     * the first character of the matched sequence and the index will be incremented past it.
     *
     * @param target    the target {@link CharSequence}
     * @return          {@code true} if the characters in the text at the index match the target
     */
    public boolean match(CharSequence target) {
        int len = target.length();
        if (index + len > text.length())
            return false;
        int i = index;
        int j = 0;
        for (; len > 0; len--)
            if (text.charAt(i++) != target.charAt(j++))
                return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the characters at the index against a given {@link CharSequence} ({@link String},
     * {@link StringBuilder} etc.), checking that the character following the match is not part
     * of a name.  Following a successful match the start index will point to
     * the first character of the matched sequence and the index will be incremented past it.
     *
     * @param target    the target {@link CharSequence}
     * @return          {@code true} if the characters in the text at the index match the target
     */
    public boolean matchName(CharSequence target) {
        int len = target.length();
        if (index + len > text.length())
            return false;
        int i = index;
        int j = 0;
        for (; len > 0; len--)
            if (text.charAt(i++) != target.charAt(j++))
                return false;
        if (i < text.length() && isNameContinuation(text.charAt(i)))
            return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the characters at the index against any of an array of {@link CharSequence}
     * ({@link String}, {@link StringBuilder} etc.) objects.  Following a successful match the
     * start index will point to the first character of the matched sequence and the index will
     * be incremented past it.
     *
     * @param array     the array of {@link CharSequence}
     * @return          {@code true} if the characters in the text at the index match any of the
     *                  entries in the array
     */
    public boolean matchAnyOf(CharSequence[] array) {
        for (CharSequence str : array)
            if (match(str))
                return true;
        return false;
    }

    /**
     * Match the characters at the index against any of a {@link Collection} of
     * {@link CharSequence} ({@link String}, {@link StringBuilder} etc.) objects.  Following a
     * successful match the start index will point to the first character of the matched
     * sequence and the index will be incremented past it.
     *
     * @param collection    the {@link Collection} of {@link CharSequence}s
     * @return              {@code true} if the characters in the text at the index match any of
     *                      the entries in the collection
     */
    public boolean matchAnyOf(Collection<? extends CharSequence> collection) {
        for (CharSequence str : collection)
            if (match(str))
                return true;
        return false;
    }

    /**
     * Match the characters at the index against a given {@link CharSequence} ({@link String},
     * {@link StringBuilder} etc.), ignoring case.  Following a successful match the start index
     * will point to the first character of the matched sequence and the index will be
     * incremented past it.
     *
     * @param target    the target {@link CharSequence}
     * @return          {@code true} if the characters in the text at the index match the target
     */
    public boolean matchIgnoreCase(CharSequence target) {
        int len = target.length();
        if (index + len > text.length())
            return false;
        int i = index;
        int j = 0;
        for (; len > 0; len--)
            if (!equalIgnoreCase(text.charAt(i++), target.charAt(j++)))
                return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the characters at the index as decimal digits, with a given minimum number of
     * digits and an optional maximum.
     *
     * @param maxDigits the maximum number digits to match (or 0 to indicate no limit)
     * @param minDigits the minimum number digits for a successful match
     * @return          {@code true} if the characters in the text at the index are decimal
     *                  digits (subject to the specified minimum and maximum number of digits)
     */
    public boolean matchDec(int maxDigits, int minDigits) {
        int i = index;
        int stopper = text.length();
        if (maxDigits > 0)
            stopper = Math.min(stopper, i + maxDigits);
        while (i < stopper && isDigit(text.charAt(i)))
            i++;
        if (i - index < minDigits)
            return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the characters at the index as decimal digits, with a minimum of 1 digit and an
     * optional maximum.
     *
     * @param maxDigits the maximum number digits to match (or 0 to indicate no limit)
     * @return          {@code true} if the characters in the text at the index are decimal
     *                  digits (subject to the specified maximum number of digits)
     */
    public boolean matchDec(int maxDigits) {
        return matchDec(maxDigits, 1);
    }

    /**
     * Match the characters at the index as decimal digits, with a minimum of 1 digit and no
     * maximum.
     *
     * @return  {@code true} if the characters in the text at the index are decimal digits
     */
    public boolean matchDec() {
        return matchDec(0, 1);
    }

    /**
     * Match the characters at the index as a fixed number of decimal digits.
     *
     * @param numDigits the number of digits expected
     * @return          {@code true} if the characters in the text at the index are decimal
     *                  digits
     */
    public boolean matchDecFixed(int numDigits) {
        return matchDec(numDigits, numDigits);
    }

    /**
     * Match the characters at the index as hexadecimal digits, with a given minimum number of
     * digits and an optional maximum.
     *
     * @param maxDigits the maximum number digits to match (or 0 to indicate no limit)
     * @param minDigits the minimum number digits for a successful match
     * @return          {@code true} if the characters in the text at the index are hexadecimal
     *                  digits (subject to the specified minimum and maximum number of digits)
     */
    public boolean matchHex(int maxDigits, int minDigits) {
        int i = index;
        int stopper = text.length();
        if (maxDigits > 0)
            stopper = Math.min(stopper, i + maxDigits);
        while (i < stopper && isHexDigit(text.charAt(i)))
            i++;
        if (i - index < minDigits)
            return false;
        start = index;
        index = i;
        return true;
    }

    /**
     * Match the characters at the index as hexadecimal digits, with a minimum of 1 digit and an
     * optional maximum.
     *
     * @param maxDigits the maximum number digits to match (or 0 to indicate no limit)
     * @return          {@code true} if the characters in the text at the index are hexadecimal
     *                  digits (subject to the specified maximum number of digits)
     */
    public boolean matchHex(int maxDigits) {
        return matchHex(maxDigits, 1);
    }

    /**
     * Match the characters at the index as hexadecimal digits, with a minimum of 1 digit and no
     * maximum.
     *
     * @return  {@code true} if the characters in the text at the index are hexadecimal digits
     */
    public boolean matchHex() {
        return matchHex(0, 1);
    }

    /**
     * Match the characters at the index as a fixed number of hexadecimal digits.
     *
     * @param numDigits the number of digits expected
     * @return          {@code true} if the characters in the text at the index are hexadecimal
     *                  digits
     */
    public boolean matchHexFixed(int numDigits) {
        return matchHex(numDigits, numDigits);
    }

    /**
     * Undo the effect of the last match operation.
     *
     * @return      the {@code ParseText} object (for chaining purposes)
     */
    public ParseText revert() {
        index = start;
        return this;
    }

    /**
     * Reset the index to the start of the text.
     *
     * @return      the {@code ParseText} object (for chaining purposes)
     */
    public ParseText reset() {
        index = 0;
        return this;
    }

    /**
     * Increment the index by <i>n</i>.
     *
     * @param n     the amount to add to the index
     * @return      the {@code ParseText} object (for chaining purposes)
     * @throws      StringIndexOutOfBoundsException if the result index would be beyond the end
     *              of the text
     */
    public ParseText skip(int n) {
        start = index;
        setIndex(index + n);
        return this;
    }

    /**
     * Decrement the index by <i>n</i>.
     *
     * @param n     the amount to subtract from the index
     * @return      the {@code ParseText} object (for chaining purposes)
     * @throws      StringIndexOutOfBoundsException if the result index would be negative
     */
    public ParseText back(int n) {
        setIndex(index - n);
        return this;
    }

    /**
     * Increment the index to the next occurrence of the given character.  The index is left
     * positioned at the matched character.
     *
     * @param   ch      the stopper character
     * @return          the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipTo(char ch) {
        int i = index;
        start = i;
        while (i < text.length() && text.charAt(i) != ch)
            i++;
        index = i;
        return this;
    }

    /**
     * Increment the index to the next occurrence of any of the given characters.  The index is
     * left positioned at the matched character.
     *
     * @param   array   the array of possible stopper characters
     * @return          the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipToAnyOf(char[] array) {
        int i = index;
        start = i;
    outer:
        while (i < text.length()) {
            char ch = text.charAt(i);
            for (int j = 0; j < array.length; j++)
                if (ch == array[j])
                    break outer;
            i++;
        }
        index = i;
        return this;
    }

    /**
     * Increment the index to the next occurrence of any of the given characters.  The index is
     * left positioned at the stopper character.
     *
     * @param   stoppers    a {@link CharSequence} of possible stopper characters
     * @return              the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipToAnyOf(CharSequence stoppers) {
        int i = index;
        start = i;
    outer:
        while (i < text.length()) {
            char ch = text.charAt(i);
            for (int j = 0; j < stoppers.length(); j++)
                if (ch == stoppers.charAt(j))
                    break outer;
            i++;
        }
        index = i;
        return this;
    }

    /**
     * Increment the index to the next occurrence of the stopper sequence.  The index is left
     * positioned at the stopper sequence.
     *
     * @param   target  the stopper sequence
     * @return          the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipTo(CharSequence target) {
        int len = target.length();
        int i = index;
        start = i;
        int stopper = text.length() - len;
    outer:
        for (;;) {
            if (i > stopper) {
                i = text.length();
                break;
            }
            int j = 0;
            for (;;) {
                if (j >= len)
                    break outer;
                if (text.charAt(i + j) != target.charAt(j))
                    break;
                j++;
            }
            i++;
        }
        index = i;
        return this;
    }

    /**
     * Match the characters at the index as spaces.
     *
     * @return  {@code true} if the characters in the text at the index are one or more spaces
     */
    public boolean matchSpaces() {
        int i = index;
        int len = text.length();
        if (i >= len || !isSpace(text.charAt(i)))
            return false;
        start = i;
        do {
            i++;
        } while (i < len && isSpace(text.charAt(i)));
        index = i;
        return true;
    }

    /**
     * Increment the index past zero or more spaces.
     *
     * @return      the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipSpaces() {
        int i = index;
        start = i;
        int len = text.length();
        while (i < len && isSpace(text.charAt(i)))
            i++;
        index = i;
        return this;
    }

    /**
     * Increment the index to the next space.
     *
     * @return  the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipToSpace() {
        int i = index;
        start = i;
        int len = text.length();
        while (i < len && !isSpace(text.charAt(i)))
            i++;
        index = i;
        return this;
    }

    /**
     * Increment the index directly to the end of the text.
     *
     * @return  the {@code ParseText} object (for chaining purposes)
     */
    public ParseText skipToEnd() {
        start = index;
        index = text.length();
        return this;
    }

    /**
     * Match the characters at the index as a name, where a name is defined as starting with a
     * character that matches {@link #isNameStart(char)}, followed by zero or more characters
     * that match {@link #isNameContinuation(char)}.
     *
     * @return  {@code true} if the characters in the text at the index constitute a name
     */
    public boolean matchName() {
        int i = index;
        int len = text.length();
        if (i >= len || !isNameStart(text.charAt(i)))
            return false;
        start = i;
        do {
            ++i;
        } while (i < len && isNameContinuation(text.charAt(i)));
        index = i;
        return true;
    }

    /**
     * Unescape a string from the index, using the provided {@link CharUnmapper} and stopping on
     * the given character stopper value.  The index is left positioned at the stopper character
     * so that the caller can test whether the stopper was present.  A call to
     * {@link #getResultString()} or {@link #getResultSequence()} following this method will
     * return the section of the text prior to unescaping.
     *
     * @param   charUnmapper    the {@link CharUnmapper}
     * @param   stopper         the stopper character (e.g. the closing quote)
     * @return  the unescaped string
     * @throws  IllegalArgumentException if thrown by the {@link CharUnmapper}
     */
    public String unescape(CharUnmapper charUnmapper, char stopper) {
        int i = index;
        start = i;
        int len = text.length();
        while (i < len) {
            char ch = text.charAt(i);
            if (ch == stopper)
                break;
            if (charUnmapper.isEscape(text, i)) {
                StringBuilder sb = new StringBuilder();
                sb.append(text, start, i);
                i += charUnmapper.unmap(sb, text, i);
                while (i < len) {
                    ch = text.charAt(i);
                    if (ch == stopper)
                        break;
                    if (charUnmapper.isEscape(text, i))
                        i += charUnmapper.unmap(sb, text, i);
                    else {
                        sb.append(ch);
                        i++;
                    }
                }
                index = i;
                return sb.toString();
            }
            i++;
        }
        index = i;
        return text.subSequence(start, i).toString();
    }

    /**
     * Test whether the given character is a space.  This method may be overridden to provide
     * for different definitions of a space.
     *
     * @param ch    the character
     * @return      {@code true} if the character is a space
     */
    public boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    /**
     * Test whether the given character is a digit.  This method may be overridden to provide
     * for different definitions of a digit.  If this method is overridden it may be necessary
     * to override {@link #convertDecDigit(char)} as well.
     *
     * @param ch    the character
     * @return      {@code true} if the character is a digit
     */
    public boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Test whether the given character is a hexadecimal digit.  This method may be overridden
     * to provide for different definitions of a hex digit.  If this method is overridden it may
     * be necessary to override {@link #convertHexDigit(char)} as well.
     *
     * @param ch    the character
     * @return      {@code true} if the character is a hexadecimal digit
     */
    public boolean isHexDigit(char ch) {
        return ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f';
    }

    /**
     * Test whether the given character is the start character of a name.  This method may be
     * overridden to provide for different definitions of a name.
     *
     * @param ch    the character
     * @return      {@code true} if the character is a name start character
     */
    public boolean isNameStart(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_' || ch == '$';
    }

    /**
     * Test whether the given character is a continuation character of a name.  This method may
     * be overridden to provide for different definitions of a name.
     *
     * @param ch    the character
     * @return      {@code true} if the character is a name continuation character
     */
    public boolean isNameContinuation(char ch) {
        return isNameStart(ch) || ch >= '0' && ch <= '9';
    }

    /**
     * Create a {@link String} representation of the {@code ParseText} object.  This is
     * primarily intended for debugging purposes; the resulting string consists of the text
     * enclosed in square brackets, with a "{@code ~}" indicating the {@code start} position and
     * a "{@code ^}" for the {@code index}.
     *
     * @return      the {@link String} representation of the object
     */
    @Override
    public String toString() {
        int n = text.length();
        StringBuilder sb = new StringBuilder(n + 4);
        sb.append('[');
        int i = 0;
        for (;;) {
            if (i == start)
                sb.append('~');
            if (i == index)
                sb.append('^');
            if (i >= n)
                break;
            sb.append(text.charAt(i++));
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Compare the {@code ParseText} with another object for equality.
     *
     * @param o     the other object
     * @return      {@code true} if the the other object is a {@code ParseText} and the objects
     *              are equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParseText))
            return false;
        ParseText pt = (ParseText)o;
        if (text.length() != pt.text.length() || index != pt.index || start != pt.start)
            return false;
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) != pt.text.charAt(i))
                return false;
        return true;
    }

    /**
     * Compute the hash code for this object (for completeness).
     *
     * @return      the hash code
     */
    @Override
    public int hashCode() {
        int result = text.length() + index + start;
        for (int i = 0; i < text.length(); i++)
            result += text.charAt(i);
        return result;
    }

}
