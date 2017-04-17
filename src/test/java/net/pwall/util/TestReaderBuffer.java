/*
 * @(#) TestReaderBuffer.java
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

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

/**
 * Test class for {@link ReaderBuffer}.
 *
 * @author Peter Wall
 */
public class TestReaderBuffer {

    private String exampleString = "The quick brown fox jumps over the lazy dog.\n" +
            "Now is the time for all good men to come to the aid of the party.\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n";
    private String emptyString = "";
    private String eightChars = "abcdefgh";

    @Test
    public void test() throws Exception {
        StringReader srdr = new StringReader(exampleString);
        ReaderBuffer rcs = new ReaderBuffer(srdr, 20);
        int n = exampleString.length();
        assertEquals(n, rcs.length());
        for (int i = 0; i < n; i++)
            assertEquals(exampleString.charAt(i), rcs.charAt(i));

        assertEquals(exampleString, rcs.toString());

        srdr = new StringReader(exampleString);
        rcs = new ReaderBuffer(srdr, 4);
        assertEquals(exampleString, rcs.toString());

        srdr = new StringReader(exampleString);
        rcs = new ReaderBuffer(srdr);
        assertEquals(exampleString, rcs.toString());

        srdr = new StringReader(emptyString);
        rcs = new ReaderBuffer(srdr);
        assertEquals(emptyString, rcs.toString());

        srdr = new StringReader(eightChars);
        rcs = new ReaderBuffer(srdr, 8);
        assertEquals(eightChars, rcs.toString());

        assertEquals("cdef", rcs.subSequence(2, 6).toString());

        assertEquals(exampleString, ReaderBuffer.toString(new StringReader(exampleString)));
    }

}
