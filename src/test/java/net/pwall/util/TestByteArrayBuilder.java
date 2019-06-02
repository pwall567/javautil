/*
 * @(#) TestByteArrayBuilder.java
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ByteArrayBuilder}.
 *
 * @author  Peter Wall
 */
public class TestByteArrayBuilder {

    private static final byte[] empty = new byte[0];
    private static final byte[] test1 = new byte[] { 0x00, (byte)0xF0, 0x23 };
    private static final byte[] test2 = new byte[] { 'A', 'b', 'c', '0', 'x', '\n' };

    @Test
    public void testConstructor() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        assertEquals(0, bab.length());
        assertArrayEquals(empty, bab.toByteArray());

        bab = new ByteArrayBuilder(1000);
        assertEquals(0, bab.length());
        assertArrayEquals(empty, bab.toByteArray());

        bab = new ByteArrayBuilder(test1);
        assertEquals(test1.length, bab.length());
        assertArrayEquals(test1, bab.toByteArray());

        ByteArrayBuilder bab2 = new ByteArrayBuilder(bab);
        assertEquals(test1.length, bab2.length());
        assertArrayEquals(test1, bab2.toByteArray());
    }

    @Test
    public void test_toByteArray() {
        ByteArrayBuilder bab = new ByteArrayBuilder(test2);
        assertEquals(test2.length, bab.length());
        assertArrayEquals(test2, bab.toByteArray());

        byte[] result1 = new byte[] { '0', 'x', '\n' };
        assertArrayEquals(result1, bab.toByteArray(3));

        byte[] result2 = new byte[] { 'b', 'c' };
        assertArrayEquals(result2, bab.toByteArray(1, 3));
    }

    @Test
    public void test_get_set() {
        ByteArrayBuilder bab = new ByteArrayBuilder(test2);
        assertEquals(test2.length, bab.length());
        assertArrayEquals(test2, bab.toByteArray());

        assertEquals('x', bab.get(4));

        assertEquals('c', bab.set(2, 'd'));

        assertEquals('d', bab.get(2));

        byte[] result1 = new byte[] { 'A', 'b', 'd', '0', 'x', '\n' };
        assertArrayEquals(result1, bab.toByteArray());
    }

    @Test
    public void test_append() throws Exception {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        assertEquals(0, bab.length());
        assertArrayEquals(empty, bab.toByteArray());

        bab.append('a');
        assertEquals(1, bab.length());
        byte[] result1 = new byte[] { 'a' };
        assertArrayEquals(result1, bab.toByteArray());

        bab.append('x');
        assertEquals(2, bab.length());
        byte[] result2 = new byte[] { 'a', 'x' };
        assertArrayEquals(result2, bab.toByteArray());

        bab.append(test1);
        assertEquals(5, bab.length());
        byte[] result3 = new byte[] { 'a', 'x', 0x00, (byte)0xF0, 0x23 };
        assertArrayEquals(result3, bab.toByteArray());

        bab = new ByteArrayBuilder();
        bab.append(test2, 1, 3);
        byte[] result4 = new byte[] { 'b', 'c' };
        assertArrayEquals(result4, bab.toByteArray());

        ByteArrayBuilder bab2 = new ByteArrayBuilder(test1);
        bab.append(bab2);
        byte[] result5 = new byte[] { 'b', 'c', 0x00, (byte)0xF0, 0x23 };
        assertArrayEquals(result5, bab.toByteArray());

        bab = new ByteArrayBuilder();
        InputStream is = new ByteArrayInputStream(test2);
        bab.append(is);
        assertArrayEquals(test2, bab.toByteArray());
    }

    @Test
    public void test_insert() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        assertEquals(0, bab.length());
        assertArrayEquals(empty, bab.toByteArray());

        bab.insert(0, 'a');
        assertEquals(1, bab.length());
        byte[] result1 = new byte[] { 'a' };
        assertArrayEquals(result1, bab.toByteArray());

        bab.insert(0, 'x');
        assertEquals(2, bab.length());
        byte[] result2 = new byte[] { 'x', 'a' };
        assertArrayEquals(result2, bab.toByteArray());

        bab.insert(1, test1);
        assertEquals(5, bab.length());
        byte[] result3 = new byte[] { 'x', 0x00, (byte)0xF0, 0x23, 'a' };
        assertArrayEquals(result3, bab.toByteArray());

        ByteArrayBuilder bab2 = new ByteArrayBuilder(test1);
        bab.insert(2, bab2);
        assertEquals(8, bab.length());
        byte[] result4 =
                new byte[] { 'x', 0x00, 0x00, (byte)0xF0, 0x23, (byte)0xF0, 0x23, 'a' };
        assertArrayEquals(result4, bab.toByteArray());
    }

}
