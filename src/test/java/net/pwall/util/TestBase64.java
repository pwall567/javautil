/*
 * @(#) TestBase64.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2018, 2019 Peter Wall
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
import java.util.Random;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Base64 encode/decode.
 */
public class TestBase64 {

    @Test
    public void test() {
        String s = "The quick brown fox etc.";
        byte[] a = s.getBytes();
        byte[] b = Base64.encode(a);
        byte[] c = Base64.decode(b);
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
        c = Base64.decode(new String(b));
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
    }

    @Test
    public void test0() {
        byte[] original = new byte[0];
        byte[] encoded = Base64.encode(original);
        assertEquals(0, encoded.length);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void test1() {
        byte[] original = new byte[] { (byte)'A' };
        byte[] encoded = Base64.encode(original);
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('Q', encoded[1]);
        assertEquals('=', encoded[2]);
        assertEquals('=', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void test2() {
        byte[] original = new byte[] { (byte)'A', (byte)'B' };
        byte[] encoded = Base64.encode(original);
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('I', encoded[2]);
        assertEquals('=', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void test3() {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C' };
        byte[] encoded = Base64.encode(original);
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void test4() {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C', (byte)'D' };
        byte[] encoded = Base64.encode(original);
        assertEquals(8, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        assertEquals('R', encoded[4]);
        assertEquals('A', encoded[5]);
        assertEquals('=', encoded[6]);
        assertEquals('=', encoded[7]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testRandom() {
        Random r = new Random();
        for (int n = 2000; n > 0; n--) {
            byte[] a = new byte[r.nextInt(2000)];
            r.nextBytes(a);
            byte[] b = Base64.encode(a);
            byte[] c = Base64.decode(b);
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
            c = Base64.decode(new String(b));
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
        }
    }

    @Test
    public void testURL() {
        String s = "The quick brown fox etc.";
        byte[] a = s.getBytes();
        byte[] b = Base64.encodeURL(a);
        byte[] c = Base64.decode(b);
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
        c = Base64.decode(new String(b));
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
    }

    @Test
    public void testURL0() {
        byte[] original = new byte[0];
        byte[] encoded = Base64.encodeURL(original);
        assertEquals(0, encoded.length);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testURL1() {
        byte[] original = new byte[] { (byte)'A' };
        byte[] encoded = Base64.encodeURL(original);
        assertEquals(2, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('Q', encoded[1]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testURL2() {
        byte[] original = new byte[] { (byte)'A', (byte)'B' };
        byte[] encoded = Base64.encodeURL(original);
        assertEquals(3, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('I', encoded[2]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testURL3() {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C' };
        byte[] encoded = Base64.encodeURL(original);
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testURL4() {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C', (byte)'D' };
        byte[] encoded = Base64.encodeURL(original);
        assertEquals(6, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        assertEquals('R', encoded[4]);
        assertEquals('A', encoded[5]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testURLRandom() {
        Random r = new Random();
        for (int n = 2000; n > 0; n--) {
            byte[] a = new byte[r.nextInt(2000)];
            r.nextBytes(a);
            byte[] b = Base64.encodeURL(a);
            byte[] c = Base64.decode(b);
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
            c = Base64.decode(new String(b));
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
        }
    }

    @Test
    public void testNPE() {
        assertThrows(NullPointerException.class, () -> Base64.encode(null));
        assertThrows(NullPointerException.class, () -> Base64.encodeURL(null));
        assertThrows(NullPointerException.class, () -> Base64.decode((byte[])null));
        assertThrows(NullPointerException.class, () -> Base64.decode((String)null));
    }

    @Test
    public void testIllegal() {
        assertThrows(IllegalArgumentException.class, () -> {
            byte[] b = new byte[] { (byte)'[', (byte)']' };
            Base64.decode(b);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            byte[] b = new byte[] { (byte)'A' };
            Base64.decode(b);
        });
        assertThrows(IllegalArgumentException.class, () -> Base64.decode("[]"));
        assertThrows(IllegalArgumentException.class, () -> Base64.decode("A"));
    }

    @Test
    public void testAppend() throws IOException {
        String s = "The quick brown fox etc.";
        byte[] a = s.getBytes();
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, a);
        byte[] c = Base64.decode(sb.toString());
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
    }

    @Test
    public void testAppend0() throws IOException {
        byte[] original = new byte[0];
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(0, encoded.length);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppend1() throws IOException {
        byte[] original = new byte[] { (byte)'A' };
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('Q', encoded[1]);
        assertEquals('=', encoded[2]);
        assertEquals('=', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppend2() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B' };
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('I', encoded[2]);
        assertEquals('=', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppend3() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C' };
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppend4() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C', (byte)'D' };
        StringBuilder sb = new StringBuilder();
        Base64.appendEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(8, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        assertEquals('R', encoded[4]);
        assertEquals('A', encoded[5]);
        assertEquals('=', encoded[6]);
        assertEquals('=', encoded[7]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendRandom() throws IOException {
        Random r = new Random();
        for (int n = 2000; n > 0; n--) {
            byte[] a = new byte[r.nextInt(2000)];
            r.nextBytes(a);
            StringBuilder sb = new StringBuilder();
            Base64.appendEncoded(sb, a);
            byte[] b = sb.toString().getBytes();
            byte[] c = Base64.decode(b);
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
            c = Base64.decode(new String(b));
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
        }
    }

    @Test
    public void testAppendURL() throws IOException {
        String s = "The quick brown fox etc.";
        byte[] a = s.getBytes();
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, a);
        byte[] c = Base64.decode(sb.toString());
        assertEquals(a.length, c.length);
        assertArrayEquals(a, c);
    }

    @Test
    public void testAppendURL0() throws IOException {
        byte[] original = new byte[0];
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(0, encoded.length);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendURL1() throws IOException {
        byte[] original = new byte[] { (byte)'A' };
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(2, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('Q', encoded[1]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendURL2() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B' };
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(3, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('I', encoded[2]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendURL3() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C' };
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(4, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendURL4() throws IOException {
        byte[] original = new byte[] { (byte)'A', (byte)'B', (byte)'C', (byte)'D' };
        StringBuilder sb = new StringBuilder();
        Base64.appendURLEncoded(sb, original);
        byte[] encoded = sb.toString().getBytes();
        assertEquals(6, encoded.length);
        assertEquals('Q', encoded[0]);
        assertEquals('U', encoded[1]);
        assertEquals('J', encoded[2]);
        assertEquals('D', encoded[3]);
        assertEquals('R', encoded[4]);
        assertEquals('A', encoded[5]);
        byte[] decoded = Base64.decode(encoded);
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
        decoded = Base64.decode(new String(encoded));
        assertEquals(original.length, decoded.length);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void testAppendURLRandom() throws IOException {
        Random r = new Random();
        for (int n = 2000; n > 0; n--) {
            byte[] a = new byte[r.nextInt(2000)];
            r.nextBytes(a);
            StringBuilder sb = new StringBuilder();
            Base64.appendURLEncoded(sb, a);
            byte[] b = sb.toString().getBytes();
            byte[] c = Base64.decode(b);
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
            c = Base64.decode(new String(b));
            assertEquals(a.length, c.length);
            assertArrayEquals(a, c);
        }
    }

}
