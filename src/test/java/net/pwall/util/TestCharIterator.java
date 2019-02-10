/*
 * @(#) TestCharIterator.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2017 Peter Wall
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharIterator}.
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
