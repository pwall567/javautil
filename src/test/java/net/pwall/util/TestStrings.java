/*
 * @(#) TestStrings.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2015 Peter Wall
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestStrings {

    @Test
    public void test_fromUTF8_1() {
        for (int i = 0; i < 0xD7FF; i++) {
            StringBuilder sb = new StringBuilder();
            try {
                Strings.appendUTF16(sb, i);
            }
            catch (IOException e) {
                // can't happen
            }
            String str1 = sb.toString();
            byte[] bb = Strings.toUTF8(str1);
            assertEquals(str1, Strings.fromUTF8(bb));
        }
    }

    @Test
    public void test_fromUTF8_2() {
        for (int i = 0xE000; i < 0x10FFFF; i++) {
            StringBuilder sb = new StringBuilder();
            try {
                Strings.appendUTF16(sb, i);
            }
            catch (IOException e) {
                // can't happen
            }
            String str1 = sb.toString();
            byte[] bb = Strings.toUTF8(str1);
            assertEquals(str1, Strings.fromUTF8(bb));
        }
    }

    @Test
    public void test_fromUTF8_iterator_1() {
        for (int i = 0; i < 0xD7FF; i++) {
            StringBuilder sb = new StringBuilder();
            try {
                Strings.appendUTF16(sb, i);
            }
            catch (IOException e) {
                // can't happen
            }
            String str1 = sb.toString();
            byte[] bb = Strings.toUTF8(str1);
            Iterator<Byte> bi = new Iterator<Byte>() {
                private int i = 0;
                @Override
                public boolean hasNext() {
                    return i < bb.length;
                }
                @Override
                public Byte next() {
                    return bb[i++];
                }
            };
            assertEquals(str1, Strings.fromUTF8(bi));
        }
    }

    @Test
    public void test_fromUTF8_bytebuffer_1() {
        for (int i = 0; i < 0xD7FF; i++) {
            StringBuilder sb = new StringBuilder();
            try {
                Strings.appendUTF16(sb, i);
            }
            catch (IOException e) {
                // can't happen
            }
            String str1 = sb.toString();
            byte[] bb = Strings.toUTF8(str1);
            ByteBuffer buf = ByteBuffer.allocate(bb.length);
            buf.put(bb);
            buf.flip();
            assertEquals(str1, Strings.fromUTF8(buf));
        }
    }

    @Test
    public void test_fromUTF8_bytebuffers_1() {
        StringBuilder sb = new StringBuilder(30000);
        for (int i = 0; i < 8192; i++) {
            try {
                Strings.appendUTF16(sb, i);
            }
            catch (IOException e) {
                // can't happen
            }
        }
        String str1 = sb.toString();
        byte[] bb = Strings.toUTF8(str1);
        List<ByteBuffer> bufList = new ArrayList<>();
        int offset = 0;
        int len = 1024;
        while (offset < bb.length) {
            if (bb.length - offset <= len) {
                ByteBuffer buf = ByteBuffer.allocate(bb.length - offset);
                buf.put(bb, offset, bb.length - offset);
                buf.flip();
                bufList.add(buf);
                break;
            }
            ByteBuffer buf = ByteBuffer.allocate(len);
            buf.put(bb, offset, len);
            buf.flip();
            bufList.add(buf);
            offset += len;
        }
        ByteBuffer[] bufArray = new ByteBuffer[bufList.size()];
        System.out.println("Number of ByteBuffer = " + bufArray.length);
        assertEquals(str1, Strings.fromUTF8(bufList.toArray(bufArray)));
    }

    @Test
    public void test_trim_String() {
        String s1 = "   xyz ";
        assertEquals("xyz", Strings.trim(s1));
        s1 = "abcd ";
        assertEquals("abcd", Strings.trim(s1));
        s1 = "     pqr";
        assertEquals("pqr", Strings.trim(s1));
        s1 = "--- abcdef ----";
        assertEquals(" abcdef ", Strings.trim(s1, ch -> ch == '-'));
        s1 = "AAAA";
        assertSame(s1, Strings.trim(s1));
    }

    @Test
    public void test_trim_CharSequence() {
        StringBuilder sb1 = new StringBuilder("   xyz ");
        assertEquals("xyz", Strings.trim(sb1).toString());
        sb1.setLength(0);
        sb1.append("abcd ");
        assertEquals("abcd", Strings.trim(sb1).toString());
        sb1.setLength(0);
        sb1.append("     pqr");
        assertEquals("pqr", Strings.trim(sb1).toString());
        sb1.setLength(0);
        sb1.append("--- abcdef ----");
        assertEquals(" abcdef ", Strings.trim(sb1, ch -> ch == '-').toString());
        sb1.setLength(0);
        sb1.append("AAAA");
        assertSame(sb1, Strings.trim(sb1));
    }

    @Test
    public void test_trimLeading_String() {
        String s1 = "   xyz ";
        assertEquals("xyz ", Strings.trimLeading(s1));
        s1 = "abcd ";
        assertSame(s1, Strings.trimLeading(s1));
        s1 = "     pqr";
        assertEquals("pqr", Strings.trimLeading(s1));
        s1 = "--- abcdef ----";
        assertEquals(" abcdef ----", Strings.trimLeading(s1, ch -> ch == '-'));
    }

    @Test
    public void test_trimLeading_CharSequence() {
        StringBuilder sb1 = new StringBuilder("   xyz ");
        assertEquals("xyz ", Strings.trimLeading(sb1).toString());
        sb1.setLength(0);
        sb1.append("abcd ");
        assertSame(sb1, Strings.trimLeading(sb1));
        sb1.setLength(0);
        sb1.append("     pqr");
        assertEquals("pqr", Strings.trimLeading(sb1).toString());
        sb1.setLength(0);
        sb1.append("--- abcdef ----");
        assertEquals(" abcdef ----", Strings.trimLeading(sb1, ch -> ch == '-').toString());
    }

    @Test
    public void test_trimTrailing_String() {
        String s1 = "   xyz ";
        assertEquals("   xyz", Strings.trimTrailing(s1));
        s1 = "abcd ";
        assertEquals("abcd", Strings.trimTrailing(s1));
        s1 = "     pqr";
        assertSame(s1, Strings.trimTrailing(s1));
        s1 = "--- abcdef ----";
        assertEquals("--- abcdef ", Strings.trimTrailing(s1, ch -> ch == '-'));
        s1 = "AAAA";
        assertSame(s1, Strings.trimTrailing(s1));
    }

    @Test
    public void test_trimTrailing_CharSequence() {
        StringBuilder sb1 = new StringBuilder("   xyz ");
        assertEquals("   xyz", Strings.trimTrailing(sb1).toString());
        sb1.setLength(0);
        sb1.append("abcd ");
        assertEquals("abcd", Strings.trimTrailing(sb1).toString());
        sb1.setLength(0);
        sb1.append("     pqr");
        assertSame(sb1, Strings.trimTrailing(sb1));
        sb1.setLength(0);
        sb1.append("--- abcdef ----");
        assertEquals("--- abcdef ", Strings.trimTrailing(sb1, ch -> ch == '-').toString());
    }

    @Test
    public void test_split_S_S() {
        String s1 = "the quick brown fox jumps over the lazy dog";
        String s2 = "the";
        String[] result = Strings.split(s1, s2);
        assertEquals(3, result.length);
        assertEquals("", result[0]);
        assertEquals(" quick brown fox jumps over ", result[1]);
        assertEquals(" lazy dog", result[2]);
        s2 = "dog";
        result = Strings.split(s1, s2);
        assertEquals(2, result.length);
        assertEquals("the quick brown fox jumps over the lazy ", result[0]);
        assertEquals("", result[1]);
        s2 = " ";
        result = Strings.split(s1, s2);
        assertEquals(9, result.length);
        assertEquals("the", result[0]);
        assertEquals("quick", result[1]);
        assertEquals("brown", result[2]);
        assertEquals("fox", result[3]);
        assertEquals("jumps", result[4]);
        assertEquals("over", result[5]);
        assertEquals("the", result[6]);
        assertEquals("lazy", result[7]);
        assertEquals("dog", result[8]);
        s2 = "*";
        result = Strings.split(s1, s2);
        assertEquals(1, result.length);
        assertEquals(s1, result[0]);
    }

    @Test
    public void test_split_ST() {
        String s1 = "the quick brown fox jumps over the lazy dog";
        String[] result = Strings.split(s1, ch -> ch == 'o');
        assertEquals(5, result.length);
        assertEquals("the quick br", result[0]);
        assertEquals("wn f", result[1]);
        assertEquals("x jumps ", result[2]);
        assertEquals("ver the lazy d", result[3]);
        assertEquals("g", result[4]);
    }

    @Test
    public void test_toIdentifier() {
        assertEquals("A", Strings.toIdentifier(0));
        assertEquals("B", Strings.toIdentifier(1));
        assertEquals("Z", Strings.toIdentifier(25));
        assertEquals("AA", Strings.toIdentifier(26));
        assertEquals("AB", Strings.toIdentifier(27));
    }

    @Test
    public void test_toEnglish() {
        assertEquals("zero", Strings.toEnglish(0));
        assertEquals("one", Strings.toEnglish(1));
        assertEquals("two", Strings.toEnglish(2));
        assertEquals("twelve", Strings.toEnglish(12));
        assertEquals("twenty", Strings.toEnglish(20));
        assertEquals("twenty-two", Strings.toEnglish(22));
        assertEquals("minus one", Strings.toEnglish(-1));
        assertEquals("two billion, one hundred and forty-seven million, " +
                "four hundred and eighty-three thousand, six hundred and forty-seven",
                Strings.toEnglish(0x7FFFFFFF));
        assertEquals("minus two billion, one hundred and forty-seven million, " +
                "four hundred and eighty-three thousand, six hundred and forty-eight",
                Strings.toEnglish(0x80000000));
    }

    @Test
    public void test_appendInt() throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = 123456789;
        Strings.appendInt(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = -1000;
        Strings.appendInt(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = Integer.MAX_VALUE;
        Strings.appendInt(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = Integer.MIN_VALUE;
        Strings.appendInt(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        for (i = -20000; i < 20000; i++) {
            sb = new StringBuilder();
            Strings.appendInt(sb, i);
            assertEquals(String.valueOf(i), sb.toString());
        }
    }

    @Test
    public void test_appendLong() throws IOException {
        StringBuilder sb = new StringBuilder();
        long i = 123456789123456789L;
        Strings.appendLong(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = -1000;
        Strings.appendLong(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = Long.MAX_VALUE;
        Strings.appendLong(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        sb = new StringBuilder();
        i = Long.MIN_VALUE;
        Strings.appendLong(sb, i);
        assertEquals(String.valueOf(i), sb.toString());
        for (i = -20000; i < 20000; i++) {
            sb = new StringBuilder();
            Strings.appendLong(sb, i);
            assertEquals(String.valueOf(i), sb.toString());
        }
    }

    @Test
    public void test_appendHex_B() throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b = 0;
        Strings.appendHex(sb, b);
        assertEquals("00", sb.toString());
        sb = new StringBuilder();
        b = 8;
        Strings.appendHex(sb, b);
        assertEquals("08", sb.toString());
        sb = new StringBuilder();
        b = (byte)0x80;
        Strings.appendHex(sb, b);
        assertEquals("80", sb.toString());
        sb = new StringBuilder();
        b = (byte)0xFF;
        Strings.appendHex(sb, b);
        assertEquals("FF", sb.toString());
    }

    @Test
    public void test_appendHex_C() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = 0;
        Strings.appendHex(sb, ch);
        assertEquals("0000", sb.toString());
        sb = new StringBuilder();
        ch = ' ';
        Strings.appendHex(sb, ch);
        assertEquals("0020", sb.toString());
        sb = new StringBuilder();
        ch = (char)0x8000;
        Strings.appendHex(sb, ch);
        assertEquals("8000", sb.toString());
        sb = new StringBuilder();
        ch = (char)0xFFFF;
        Strings.appendHex(sb, ch);
        assertEquals("FFFF", sb.toString());
    }

    @Test
    public void test_appendHex_I() throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Strings.appendHex(sb, i);
        assertEquals("00000000", sb.toString());
        sb = new StringBuilder();
        i = 64;
        Strings.appendHex(sb, i);
        assertEquals("00000040", sb.toString());
        sb = new StringBuilder();
        i = 65537;
        Strings.appendHex(sb, i);
        assertEquals("00010001", sb.toString());
        sb = new StringBuilder();
        i = -1;
        Strings.appendHex(sb, i);
        assertEquals("FFFFFFFF", sb.toString());
    }

    @Test
    public void test_appendHex_I2() throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Strings.appendHex(sb, i, 3);
        assertEquals("000", sb.toString());
        sb = new StringBuilder();
        i = 64;
        Strings.appendHex(sb, i, 10);
        assertEquals("0000000040", sb.toString());
        sb = new StringBuilder();
        i = 65537;
        Strings.appendHex(sb, i, 5);
        assertEquals("10001", sb.toString());
        sb = new StringBuilder();
        i = -1;
        Strings.appendHex(sb, i, 10);
        assertEquals("00FFFFFFFF", sb.toString());
    }

    @Test
    public void test_appendHex_L() throws IOException {
        StringBuilder sb = new StringBuilder();
        long m = 0;
        Strings.appendHex(sb, m);
        assertEquals("0000000000000000", sb.toString());
        sb = new StringBuilder();
        m = 64;
        Strings.appendHex(sb, m);
        assertEquals("0000000000000040", sb.toString());
        sb = new StringBuilder();
        m = 65537;
        Strings.appendHex(sb, m);
        assertEquals("0000000000010001", sb.toString());
        sb = new StringBuilder();
        m = -1;
        Strings.appendHex(sb, m);
        assertEquals("FFFFFFFFFFFFFFFF", sb.toString());
    }

    @Test
    public void test_appendHex_L2() throws IOException {
        StringBuilder sb = new StringBuilder();
        long m = 0;
        Strings.appendHex(sb, m, 3);
        assertEquals("000", sb.toString());
        sb = new StringBuilder();
        m = 64;
        Strings.appendHex(sb, m, 20);
        assertEquals("00000000000000000040", sb.toString());
        sb = new StringBuilder();
        m = 65537;
        Strings.appendHex(sb, m, 7);
        assertEquals("0010001", sb.toString());
        sb = new StringBuilder();
        m = -1;
        Strings.appendHex(sb, m, 40);
        assertEquals("000000000000000000000000FFFFFFFFFFFFFFFF", sb.toString());
    }

    @Test
    public void test_toHex_byteArray_c() {
        byte[] array = { 0x01, 0x02, 0x40, 0x41 };
        assertEquals("01.02.40.41", Strings.toHex(array, '.'));
    }

    @Test
    public void test_strip() {
        assertEquals("thequickbrownfoxetc.",
                Strings.strip("the quick brown fox etc. ", Character::isWhitespace));
        assertEquals("ok", Strings.strip("   o   k   ", Character::isWhitespace));
        assertEquals("\uD83D\uDE02\uD83D\uDE02",
                Strings.stripUTF16("   \uD83D\uDE02   \uD83D\uDE02   ",
                        Character::isWhitespace));
    }

}
