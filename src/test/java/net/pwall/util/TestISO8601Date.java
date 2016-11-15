/*
 * @(#) TestISO8601Date.java
 */

package net.pwall.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Test class for ISO8601Date.
 */
public class TestISO8601Date {

    @Test
    public void test() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
        cal.clear();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 10);
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 8);
        cal.set(Calendar.SECOND, 32);
        assertEquals("2016-11-15T14:08:32.000+11:00", ISO8601Date.toString(cal, true, -1));
    }

}
