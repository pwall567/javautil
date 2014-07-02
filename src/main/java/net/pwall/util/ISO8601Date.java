/*
 * @(#) ISO8601Date.java
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

import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * ISO 8601 Date.
 *
 * @author Peter Wall
 */
public class ISO8601Date extends Date {

    private static final long serialVersionUID = 7662874565511305042L;

    private static final int[] parseDays = { -1, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY,
            Calendar.SUNDAY, -1, -1 }; // corresponding to input day numbers 0 - 9 (-1 == error)

    private static final char dateSeparator = '-';
    private static final char weekNumberSeparator = 'W';
    private static final char dateTimeSeparator = 'T';
    private static final char zeroTimeZoneIndicator = 'Z';
    private static final char timeSeparator = ':';
    private static final char commaDecimalSeparator = ',';
    private static final char dotDecimalSeparator = '.';
    private static final char defaultDecimalSeparator =
            (new DecimalFormatSymbols()).getDecimalSeparator();
    private static final char[] decimalSeparators =
            { commaDecimalSeparator, dotDecimalSeparator };
    private static final char plusSign = '+';
    private static final char minusSign = '-';
    private static final char[] plusOrMinus = { plusSign, minusSign };

    private TimeZone timeZone;

    public ISO8601Date() {
        timeZone = null;
    }

    public ISO8601Date(long date) {
        super(date);
        timeZone = null;
    }

    public ISO8601Date(String date) {
        this(decode(date));
    }

    public ISO8601Date(String date, TimeZone timeZone) {
        super(decode(date).getTimeInMillis());
        setTimeZone(timeZone);
    }

    public ISO8601Date(Date date, TimeZone timeZone) {
        super(date.getTime());
        setTimeZone(timeZone);
    }

    public ISO8601Date(Calendar cal) {
        super(cal.getTimeInMillis());
        setTimeZone(cal.getTimeZone());
    }

    public ISO8601Date(ISO8601Date date) {
        this(date, date.getTimeZone());
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void setTime(long time) {
        super.setTime(time);
        timeZone = null;
    }

    @Override
    public String toString() {
        return toString(this, this.timeZone);
    }

    public static String toString(Date date, TimeZone timeZone) {
        StringBuilder sb = new StringBuilder(10);
        Calendar cal =
                Calendar.getInstance(timeZone != null ? timeZone : TimeZone.getDefault());
        cal.setTime(date);
        sb.append(cal.get(Calendar.YEAR));
        sb.append(dateSeparator);
        append2Digit(sb, cal.get(Calendar.MONTH) + 1);
        sb.append(dateSeparator);
        append2Digit(sb, cal.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
    }

    public static String toString(Date date) {
        return toString(date, null);
    }

    /**
     * Get a {@link Calendar} object in a suitable form for ISO 8601 dates.
     *
     * @return  {@link Calendar} object
     */
    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }

    /**
     * Parse an ISO 8601 date into a {@link Calendar} object.
     *
     * @param   str     the input date in {@link CharSequence} (e.g. {@link String}) format
     * @return          a {@link Calendar} object containing the date details from the input
     * @throws          IllegalArgumentException if the input date is invalid
     */
    public static Calendar decode(CharSequence str) {
        // note - the method name "parse" was already taken (and deprecated)
        if (str == null)
            throw new IllegalArgumentException("ISO8601 string must not be null");
        parse:
        {
            Calendar cal = getCalendar();
            cal.clear();
            boolean complete = false;
            ParseText text = new ParseText(str);
            if (!text.matchDecFixed(4))
                break parse;
            cal.set(Calendar.YEAR, text.getResultInt());
            if (!text.isExhausted()) {
                if (text.match(dateSeparator)) {
                    if (text.match(weekNumberSeparator)) {
                        if (!text.matchDecFixed(2))
                            break parse;
                        cal.set(Calendar.WEEK_OF_YEAR, text.getResultInt());
                        if (text.match(dateSeparator)) {
                            if (!text.matchDecFixed(1))
                                break parse;
                            cal.set(Calendar.DAY_OF_WEEK, parseDays[text.getResultInt()]);
                            complete = true;
                        }
                    }
                    else if (text.matchDecFixed(3)) {
                        cal.set(Calendar.DAY_OF_YEAR, text.getResultInt());
                        complete = true;
                    }
                    else {
                        if (!text.matchDecFixed(2))
                            break parse;
                        cal.set(Calendar.MONTH, text.getResultInt() - 1);
                        if (text.match(dateSeparator)) {
                            if (!text.matchDecFixed(2))
                                break parse;
                            cal.set(Calendar.DAY_OF_MONTH, text.getResultInt());
                            complete = true;
                        }
                    }
                    if (complete && text.match(dateTimeSeparator)) {
                        if (!text.matchDecFixed(2))
                            break parse;
                        cal.set(Calendar.HOUR_OF_DAY, text.getResultInt());
                        if (text.match(timeSeparator)) {
                            if (!text.matchDecFixed(2))
                                break parse;
                            cal.set(Calendar.MINUTE, text.getResultInt());
                            if (text.match(timeSeparator)) {
                                if (!text.matchDecFixed(2))
                                    break parse;
                                cal.set(Calendar.SECOND, text.getResultInt());
                                if (text.matchAnyOf(decimalSeparators))
                                    fractionSeconds(cal, text);
                            }
                            else if (text.matchAnyOf(decimalSeparators))
                                fractionMinutes(cal, text);
                        }
                        else if (text.matchAnyOf(decimalSeparators))
                            fractionHours(cal, text);
                    }
                    if (text.match(zeroTimeZoneIndicator))
                        cal.set(Calendar.ZONE_OFFSET, 0);
                    else if (text.matchAnyOf(plusOrMinus)) {
                        char sign = text.getResultChar();
                        if (!text.matchDecFixed(2))
                            break parse;
                        int mins = text.getResultInt() * 60;
                        if (text.match(timeSeparator)) {
                            if (!text.matchDecFixed(2))
                                break parse;
                            mins += text.getResultInt();
                        }
                        if (sign == minusSign)
                            mins = -mins;
                        cal.set(Calendar.ZONE_OFFSET, mins * 60 * 1_000);
                    }
                }
                else {
                    if (text.match(weekNumberSeparator)) {
                        if (!text.matchDecFixed(2))
                            break parse;
                        cal.set(Calendar.WEEK_OF_YEAR, text.getResultInt());
                        if (text.matchDecFixed(1)) {
                            cal.set(Calendar.DAY_OF_WEEK, parseDays[text.getResultInt()]);
                            complete = true;
                        }
                    }
                    else if (text.matchDecFixed(4)) {
                        int i = text.getResultInt();
                        cal.set(Calendar.MONTH, i / 100 - 1);
                        cal.set(Calendar.DAY_OF_MONTH, i % 100);
                        complete = true;
                    }
                    else if (text.matchDecFixed(3)) {
                        cal.set(Calendar.DAY_OF_YEAR, text.getResultInt());
                        complete = true;
                    }
                    else if (text.matchDecFixed(2)) {
                        cal.set(Calendar.MONTH, text.getResultInt() - 1);
                    }
                    if (complete && text.match(dateTimeSeparator)) {
                        if (!text.matchDecFixed(2))
                            break parse;
                        cal.set(Calendar.HOUR_OF_DAY, text.getResultInt());
                        if (text.matchDecFixed(2)) {
                            cal.set(Calendar.MINUTE, text.getResultInt());
                            if (text.matchDecFixed(2)) {
                                cal.set(Calendar.SECOND, text.getResultInt());
                                if (text.matchAnyOf(decimalSeparators))
                                    fractionSeconds(cal, text);
                            }
                            else if (text.matchAnyOf(decimalSeparators))
                                fractionMinutes(cal, text);
                        }
                        else if (text.matchAnyOf(decimalSeparators))
                            fractionHours(cal, text);
                    }
                    if (text.match(zeroTimeZoneIndicator))
                        cal.set(Calendar.ZONE_OFFSET, 0);
                    else if (text.matchAnyOf(plusOrMinus)) {
                        char sign = text.getResultChar();
                        if (!text.matchDecFixed(2))
                            break parse;
                        int mins = text.getResultInt() * 60;
                        if (text.matchDecFixed(2))
                            mins += text.getResultInt();
                        if (sign == minusSign)
                            mins = -mins;
                        cal.set(Calendar.ZONE_OFFSET, mins * 60 * 1_000);
                    }
                }
                if (!text.isExhausted())
                    break parse;
            }
            return cal;
        }
        throw new IllegalArgumentException("Illegal ISO8601 date string");
    }

    private static void fractionHours(Calendar cal, ParseText text) {
        checkFraction(text);
        long millis = adjustFraction(text.getResultLong() * 3_600, text.getResultLength());
        cal.set(Calendar.MINUTE, (int)(millis / 60_000));
        millis %= 60_000;
        if (millis != 0) {
            cal.set(Calendar.SECOND, (int)(millis / 1_000));
            millis %= 1_000;
            if (millis != 0)
                cal.set(Calendar.MILLISECOND, (int)millis);
        }
    }

    private static void fractionMinutes(Calendar cal, ParseText text) {
        checkFraction(text);
        long millis = adjustFraction(text.getResultLong() * 60, text.getResultLength());
        cal.set(Calendar.SECOND, (int)(millis / 1_000));
        millis %= 1_000;
        if (millis != 0)
            cal.set(Calendar.MILLISECOND, (int)millis);
    }

    private static void fractionSeconds(Calendar cal, ParseText text) {
        checkFraction(text);
        cal.set(Calendar.MILLISECOND,
                (int)adjustFraction(text.getResultLong(), text.getResultLength()));
    }

    private static void checkFraction(ParseText text) {
        if (!text.matchDec(9))
            throw new IllegalArgumentException("Illegal fraction in ISO8601 date string");
    }

    private static long adjustFraction(long value, int len) {
        if (len > 3) {
            for (; len > 4; len--)
                value /= 10;
            value = (value + 5) / 10;
        }
        else {
            for (; len < 3; len++)
                value *= 10;
        }
        return value;
    }

    /**
     * Convert a {@link Calendar} object to an ISO 8601 string.  This function outputs to the
     * string only those fields that are marked as having been set explicitly.
     *
     * <p>The "extended" format will be used, that is, date and time separators will be
     * included.</p>
     *
     * @param   cal         a {@link Calendar} object
     * @return  the date represented by the {@link Calendar} in ISO 8601 format
     */
    public static String toString(Calendar cal) {
        return toString(cal, true);
    }

    /**
     * Convert a {@link Calendar} object to an ISO 8601 string.  This function outputs to the
     * string only those fields that are marked as having been set explicitly.
     *
     * @param   cal         a {@link Calendar} object
     * @param   extended    if {@code true}, use "extended" format (include date and time
     *                      separators)
     * @return  the date represented by the {@link Calendar} in ISO 8601 format
     */
    public static String toString(Calendar cal, boolean extended) {
        boolean yearSet = cal.isSet(Calendar.YEAR);
        boolean monthSet = cal.isSet(Calendar.MONTH);
        boolean dayOfMonthSet = cal.isSet(Calendar.DAY_OF_MONTH);
        boolean weekOfYearSet = cal.isSet(Calendar.WEEK_OF_YEAR);
        boolean dayOfWeekSet = cal.isSet(Calendar.DAY_OF_WEEK);
        boolean dayOfYearSet = cal.isSet(Calendar.DAY_OF_YEAR);
        boolean hourOfDaySet = cal.isSet(Calendar.HOUR_OF_DAY);
        boolean minuteSet = cal.isSet(Calendar.MINUTE);
        boolean secondSet = cal.isSet(Calendar.SECOND);
        boolean millisecondSet = cal.isSet(Calendar.MILLISECOND);
        boolean zoneOffsetSet = cal.isSet(Calendar.ZONE_OFFSET);
        StringBuilder sb = new StringBuilder();
        if (yearSet) {
            sb.append(cal.get(Calendar.YEAR));
            if (monthSet) {
                if (extended)
                    sb.append(dateSeparator);
                append2Digit(sb, cal.get(Calendar.MONTH) + 1);
                if (dayOfMonthSet) {
                    if (extended)
                        sb.append(dateSeparator);
                    append2Digit(sb, cal.get(Calendar.DAY_OF_MONTH));
                    if (hourOfDaySet || minuteSet || secondSet || millisecondSet) {
                        sb.append(dateTimeSeparator);
                        appendTime(sb, cal, extended, minuteSet, secondSet, millisecondSet);
                    }
                }
            }
            else if (weekOfYearSet) {
                if (extended)
                    sb.append(dateSeparator);
                sb.append(weekNumberSeparator);
                append2Digit(sb, cal.get(Calendar.WEEK_OF_YEAR));
                if (dayOfWeekSet) {
                    int d = cal.get(Calendar.DAY_OF_WEEK);
                    for (int i = 1; i < 8; i++) {
                        if (parseDays[i] == d) {
                            if (extended)
                                sb.append(dateSeparator);
                            sb.append(i);
                            break;
                        }
                    }
                    if (hourOfDaySet || minuteSet || secondSet || millisecondSet) {
                        sb.append(dateTimeSeparator);
                        appendTime(sb, cal, extended, minuteSet, secondSet, millisecondSet);
                    }
                }
            }
            else if (dayOfYearSet) {
                if (extended)
                    sb.append(dateSeparator);
                append3Digit(sb, cal.get(Calendar.DAY_OF_YEAR));
                if (hourOfDaySet || minuteSet || secondSet || millisecondSet) {
                    sb.append(dateTimeSeparator);
                    appendTime(sb, cal, extended, minuteSet, secondSet, millisecondSet);
                }
            }
        }
        else
            appendTime(sb, cal, extended, minuteSet, secondSet, millisecondSet);
        if (zoneOffsetSet) {
            int mins = cal.get(Calendar.ZONE_OFFSET) / 60_000;
            if (mins == 0)
                sb.append(zeroTimeZoneIndicator);
            else {
                sb.append(mins < 0 ? minusSign : plusSign);
                mins = Math.abs(mins);
                append2Digit(sb, mins / 60);
                mins %= 60;
                if (extended)
                    sb.append(timeSeparator);
                append2Digit(sb, mins);
            }
        }
        return sb.toString();
    }

    private static void appendTime(StringBuilder sb, Calendar cal, boolean extended,
            boolean minuteSet, boolean secondSet, boolean millisecondSet) {
        append2Digit(sb, cal.get(Calendar.HOUR_OF_DAY));
        if (minuteSet || secondSet || millisecondSet) {
            if (extended)
                sb.append(timeSeparator);
            append2Digit(sb, cal.get(Calendar.MINUTE));
            if (secondSet || millisecondSet) {
                if (extended)
                    sb.append(timeSeparator);
                append2Digit(sb, cal.get(Calendar.SECOND));
                if (millisecondSet) {
                    sb.append(defaultDecimalSeparator);
                    append3Digit(sb, cal.get(Calendar.MILLISECOND));
                }
            }
        }
    }

    private static void append2Digit(StringBuilder sb, int n) {
        n = Math.abs(n) % 100;
        if (n < 10)
            sb.append('0');
        sb.append(n);
    }

    private static void append3Digit(StringBuilder sb, int n) {
        n = Math.abs(n) % 1_000;
        if (n < 100) {
            sb.append('0');
            if (n < 10)
                sb.append('0');
        }
        sb.append(n);
    }

}
