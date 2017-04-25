/*
 * @(#) TestCharIterator.java
 */

package net.pwall.util;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Default comment for {@code TestCharIterator}.
 *
 * @author  Peter Wall
 */
public class TestCharIterator {

    private static final String test1 = "";
    private static final String test2 = "Now is the time...";

    @Test
    public void test() {
        CharIterator ci = new CharIterator(test1);
        assertFalse(ci.hasNext());

        ci = new CharIterator(test2);
        assertTrue(ci.hasNext());
        assertEquals('N', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('o', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('w', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals(' ', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('i', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('s', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals(' ', ci.next().charValue());
        assertEquals('t', ci.next().charValue());
        assertEquals('h', ci.next().charValue());
        assertEquals('e', ci.next().charValue());
        assertEquals(' ', ci.next().charValue());
        assertEquals('t', ci.next().charValue());
        assertEquals('i', ci.next().charValue());
        assertEquals('m', ci.next().charValue());
        assertEquals('e', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('.', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('.', ci.next().charValue());
        assertTrue(ci.hasNext());
        assertEquals('.', ci.next().charValue());
        assertFalse(ci.hasNext());
    }

}
