/*
 * @(#) TestParseText.java
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParseText}.
 */
public class TestParseText {

    private static String str1 = "abc,d;e";
    private static String str2 = "   12345   ";
    private int smile = 0x1F609;

    @Test
    public void testConstructor() {
        ParseText pt = new ParseText(str1);
        assertEquals(str1, pt.getText());
        assertEquals(7, pt.length());
        assertEquals(0, pt.getIndex());
        assertEquals(0, pt.getStart());

        pt = new ParseText(str1, 3);
        assertEquals(str1, pt.getText());
        assertEquals(7, pt.length());
        assertEquals(3, pt.getIndex());
        assertEquals(3, pt.getStart());
    }

    @Test
    public void testConstructorNull() {
        assertThrows(NullPointerException.class, () -> new ParseText(null));
    }

    @Test
    public void testSetText() {
        ParseText pt = new ParseText(str1);
        pt.setText(str2);
        assertEquals(str2, pt.getText());
        assertEquals(0, pt.getIndex());
        assertEquals(0, pt.getStart());

        pt.setText(str2, 3);
        assertEquals(str2, pt.getText());
        assertEquals(3, pt.getIndex());
        assertEquals(3, pt.getStart());
    }

    @Test
    public void testVarious() {
        ParseText pt = new ParseText(str1);
        assertEquals(7, pt.getTextLength());
        assertEquals(0, pt.getIndex());
        assertFalse(pt.isExhausted());
        assertEquals('a', pt.getChar());

        pt.setIndex(2);
        assertEquals(2, pt.getIndex());
        assertFalse(pt.isExhausted());
        assertEquals('c', pt.getChar());

        pt.setIndex(7);
        assertEquals(7, pt.getIndex());
        assertTrue(pt.isExhausted());

        assertEquals("abc", pt.getString(0, 3));
        assertEquals("d;", pt.getString(4, 6));

        pt.setStart(2);
        assertEquals(2, pt.getStart());

        assertEquals(5, pt.getResultLength());
        assertEquals('c', pt.getResultChar());

        assertEquals("c,d;e", pt.getResultSequence().toString());
        assertEquals("c,d;e", pt.getResultString());

        pt.setIndex(5);
        assertTrue(pt.available(1));
        assertTrue(pt.available(2));
        assertFalse(pt.available(3));

        assertEquals('d', pt.charAt(4));

        pt.revert();
        assertEquals(2, pt.getIndex());

        pt.reset();
        assertEquals(0, pt.getIndex());

        pt.skip(3);
        assertEquals(3, pt.getIndex());
        pt.skip(2);
        assertEquals(5, pt.getIndex());
        pt.back(3);
        assertEquals(2, pt.getIndex());
    }

    @Test
    public void testAppend() throws IOException {
        ParseText pt = new ParseText(str1);
        pt.setIndex(5);
        pt.setStart(2);
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        pt.appendResultTo(sb);
        sb.append(']');
        assertEquals("[c,d]", sb.toString());

        StringWriter sw = new StringWriter();
        sw.append('{');
        pt.appendResultTo(sw);
        sw.append('}');
        assertEquals("{c,d}", sw.toString());
    }

    @Test
    public void testUTF16() throws IOException {
        StringBuilder sb = new StringBuilder();
        Strings.appendUTF16(sb, smile);
        sb.append('!');
        ParseText pt = new ParseText(sb);
        assertEquals(3, pt.getTextLength());
        assertEquals(smile, pt.getCodePoint());
    }

    @Test
    public void testSetIndexError() {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.setIndex(8);
        });
    }

    @Test
    public void testSetStartError() {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.setStart(1);
        });
    }

    @Test
    public void testGetInt() {
        ParseText pt = new ParseText(str2);
        assertEquals(12345, pt.getInt(3, 8));
        assertEquals(1234, pt.getInt(3, 7));
        assertEquals(234, pt.getInt(4, 7));

        pt.setText("2147483647");
        assertEquals(2147483647, pt.getInt(0, 10));

        pt.setText("2147483645");
        assertEquals(2147483645, pt.getInt(0, 10));
    }

    @Test
    public void testGetIntError() {
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("2147483648");
            pt.getInt(0, 10);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("2147483650");
            pt.getInt(0, 10);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("  47483650");
            pt.getInt(0, 10);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.getInt(0, 0);
        });
    }

    @Test
    public void testGetResultInt() {
        ParseText pt = new ParseText(str2);
        pt.setIndex(8);
        pt.setStart(3);
        assertEquals(12345, pt.getResultInt());
        pt.setIndex(7);
        assertEquals(1234, pt.getResultInt());
        pt.setStart(4);
        assertEquals(234, pt.getResultInt());
    }

    @Test
    public void testGetHexInt() {
        ParseText pt = new ParseText(str2);
        assertEquals(0x12345, pt.getHexInt(3, 8));
        assertEquals(0x1234, pt.getHexInt(3, 7));
        assertEquals(0x234, pt.getHexInt(4, 7));

        pt.setText(str1);
        assertEquals(0xABC, pt.getHexInt(0, 3));

        pt.setText("7FFFFFFF");
        assertEquals(0x7FFFFFFF, pt.getHexInt(0, 8));

        pt.setText("7FFFFFF0");
        assertEquals(0x7FFFFFF0, pt.getHexInt(0, 8));
    }

    @Test
    public void testGetHexIntError() {
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("80000000");
            pt.getHexInt(0, 8);
        });
    }

    @Test
    public void testGetResultHexInt() {
        ParseText pt = new ParseText(str2);
        pt.setIndex(8);
        pt.setStart(3);
        assertEquals(0x12345, pt.getResultHexInt());
        pt.setIndex(7);
        assertEquals(0x1234, pt.getResultHexInt());
        pt.setStart(4);
        assertEquals(0x234, pt.getResultHexInt());
    }

    @Test
    public void testGetLong() {
        ParseText pt = new ParseText(str2);
        assertEquals(12345L, pt.getLong(3, 8));
        assertEquals(1234L, pt.getLong(3, 7));
        assertEquals(234L, pt.getLong(4, 7));

        pt.setText("2147483647");
        assertEquals(2147483647L, pt.getLong(0, 10));

        pt.setText("2147483645");
        assertEquals(2147483645L, pt.getLong(0, 10));

        pt.setText("9223372036854775807");
        assertEquals(9223372036854775807L, pt.getLong(0, 19));

        pt.setText("9223372036854775805");
        assertEquals(9223372036854775805L, pt.getLong(0, 19));
    }

    @Test
    public void testGetLongError() {
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("9223372036854775808");
            pt.getLong(0, 19);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("9223372036854775810");
            pt.getLong(0, 19);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("+223372036854775807");
            pt.getLong(0, 19);
        });
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.getLong(0, 0);
        });
    }

    @Test
    public void testGetHexLong() {
        ParseText pt = new ParseText(str2);
        assertEquals(0x12345L, pt.getHexLong(3, 8));
        assertEquals(0x1234L, pt.getHexLong(3, 7));
        assertEquals(0x234L, pt.getHexLong(4, 7));

        pt.setText(str1);
        assertEquals(0xABCL, pt.getHexLong(0, 3));

        pt.setText("7FFFFFFFFFFFFFFF");
        assertEquals(0x7FFFFFFFFFFFFFFFL, pt.getHexLong(0, 16));

        pt.setText("7FFFFFFFFFFFFFF0");
        assertEquals(0x7FFFFFFFFFFFFFF0L, pt.getHexLong(0, 16));
    }

    @Test
    public void testGetHexLongError() {
        assertThrows(NumberFormatException.class, () -> {
            ParseText pt = new ParseText("8000000000000000");
            pt.getHexLong(0, 16);
        });
    }

    @Test
    public void testGetResultHexLong() {
        ParseText pt = new ParseText(str2);
        pt.setIndex(8);
        pt.setStart(3);
        assertEquals(0x12345L, pt.getResultHexLong());
        pt.setIndex(7);
        assertEquals(0x1234L, pt.getResultHexLong());
        pt.setStart(4);
        assertEquals(0x234L, pt.getResultHexLong());
    }

    @Test
    public void testMatchChar() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.match('A'));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.match('a'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.match('a'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.match('b'));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());

        assertTrue(pt.matchIgnoreCase('C'));
        assertEquals(3, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @Test
    public void testMatchCodepoint() throws IOException {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.match((int)'A'));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.match((int)'a'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.match((int)'a'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.match((int)'b'));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());

        StringBuilder sb = new StringBuilder();
        Strings.appendUTF16(sb, smile);
        sb.append('!');
        pt = new ParseText(sb);
        assertEquals(0, pt.getIndex());

        assertFalse(pt.match((int)'A'));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.match(smile));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());
    }

    @Test
    public void testMatchRange() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchRange('0', '9'));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchRange('a', 'z'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchRange('a', 'z'));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());
    }

    @Test
    public void testMatchAnyOf1() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf("0123456789"));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf("abcd"));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf("abcd"));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());
    }

    @Test
    public void testMatchAnyOf2() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf('a', 'b', 'c', 'd'));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf('a', 'b', 'c', 'd'));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());
    }

    @Test
    public void testMatchAnyOf3() {
        char[] array1 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        char[] array2 = { 'a', 'b', 'c', 'd' };

        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf(array1));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf(array2));
        assertEquals(1, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf(array2));
        assertEquals(2, pt.getIndex());
        assertEquals(1, pt.getStart());
    }

    @Test
    public void testMatchAnyOf4() {
        assertThrows(IllegalArgumentException.class, () -> {
            char[] array1 = {};
            ParseText pt = new ParseText(str1);
            assertFalse(pt.matchAnyOf(array1));
        });
    }

    @Test
    public void testMatchCS() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.match("abcd"));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.match("ab"));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.match("c,d;"));
        assertEquals(6, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @Test
    public void testMatchName() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchName("ab"));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchName("abc"));
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());
    }

    @Test
    public void testMatchAnyOfCS1() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf("aa", "bb"));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf("cd", "ab"));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf("a", "b", "c"));
        assertEquals(3, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @Test
    public void testMatchAnyOfCS2() {
        String[] array1 = { "aa", "bb" };
        String[] array2 = { "cd", "ab" };
        String[] array3 = { "a", "b", "c" };

        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf(array1));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf(array2));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf(array3));
        assertEquals(3, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @Test
    public void testMatchAnyOfCS3() {
        List<String> list1 = createList("aa", "bb");
        List<String> list2 = createList("cd", "ab");
        List<String> list3 = createList("a", "b", "c");

        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchAnyOf(list1));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchAnyOf(list2));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchAnyOf(list3));
        assertEquals(3, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @SafeVarargs
    private final <T> List<T> createList(@SuppressWarnings("unchecked") T... items) {
        List<T> result = new ArrayList<>();
        Collections.addAll(result, items);
        return result;
    }

    @Test
    public void testMatchIgnoreCaseCS() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchIgnoreCase("ABCD"));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchIgnoreCase("Ab"));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertTrue(pt.matchIgnoreCase("C,D;"));
        assertEquals(6, pt.getIndex());
        assertEquals(2, pt.getStart());
    }

    @Test
    public void testMatchDec() {
        ParseText pt = new ParseText(str2);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchDec(0, 1));
        assertEquals(0, pt.getIndex());

        pt.setIndex(3);
        assertTrue(pt.matchDec(0, 1));
        assertEquals(8, pt.getIndex());
        assertEquals(3, pt.getStart());

        assertFalse(pt.matchDec(0, 1));

        pt.setStart(0);
        pt.setIndex(3);
        assertEquals(3, pt.getIndex());
        assertFalse(pt.matchDec(0, 6));
        assertEquals(3, pt.getIndex());

        assertTrue(pt.matchDec(4, 1));
        assertEquals(7, pt.getIndex());
        assertEquals(3, pt.getStart());

        pt.setStart(0);
        pt.setIndex(3);
        assertEquals(3, pt.getIndex());
        assertTrue(pt.matchDec(2, 2));
        assertEquals(5, pt.getIndex());
        assertEquals(3, pt.getStart());
    }

    @Test
    public void testMatchDec2() {
        ParseText pt = new ParseText(str2);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchDec());
        assertEquals(0, pt.getIndex());

        pt.setIndex(3);
        assertTrue(pt.matchDec());
        assertEquals(8, pt.getIndex());
        assertEquals(3, pt.getStart());

        assertFalse(pt.matchDec());

        pt.setStart(0);
        pt.setIndex(3);
        assertEquals(3, pt.getIndex());
        assertFalse(pt.matchDec(0, 6));
        assertEquals(3, pt.getIndex());

        assertTrue(pt.matchDec(4));
        assertEquals(7, pt.getIndex());
        assertEquals(3, pt.getStart());

        pt.setStart(0);
        pt.setIndex(3);
        assertEquals(3, pt.getIndex());
        assertTrue(pt.matchDecFixed(2));
        assertEquals(5, pt.getIndex());
        assertEquals(3, pt.getStart());
    }

    @Test
    public void testMatchHex() {
        ParseText pt = new ParseText(str1, 3);
        assertEquals(3, pt.getIndex());
        assertFalse(pt.matchHex(0, 1));
        assertEquals(3, pt.getIndex());

        pt.setIndex(0);
        assertTrue(pt.matchHex(0, 1));
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.matchHex(0, 1));

        pt.setStart(0);
        pt.setIndex(0);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchHex(0, 4));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchHex(3, 1));
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        pt.setStart(0);
        pt.setIndex(0);
        assertEquals(0, pt.getIndex());
        assertTrue(pt.matchHex(2, 2));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());
    }

    @Test
    public void testMatchHex2() {
        ParseText pt = new ParseText(str1, 3);
        assertEquals(3, pt.getIndex());
        assertFalse(pt.matchHex());
        assertEquals(3, pt.getIndex());

        pt.setIndex(0);
        assertTrue(pt.matchHex());
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.matchHex());

        pt.setStart(0);
        pt.setIndex(0);
        assertEquals(0, pt.getIndex());
        assertFalse(pt.matchHex(0, 4));
        assertEquals(0, pt.getIndex());

        assertTrue(pt.matchHex(3));
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        pt.setStart(0);
        pt.setIndex(0);
        assertEquals(0, pt.getIndex());
        assertTrue(pt.matchHexFixed(2));
        assertEquals(2, pt.getIndex());
        assertEquals(0, pt.getStart());
    }

    @Test
    public void testSkipTo() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        pt.skipTo(',');
        assertEquals(3, pt.getIndex());

        pt.skipTo(',');
        assertEquals(3, pt.getIndex());

        pt.skip(1);
        pt.skipTo(',');
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testSkipToAnyOf1() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        pt.skipToAnyOf(';', ',');
        assertEquals(3, pt.getIndex());

        pt.skipToAnyOf(';', ',');
        assertEquals(3, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(';', ',');
        assertEquals(5, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(';', ',');
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testSkipToAnyOf2() {
        char[] array = { ';', ',' };

        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        pt.skipToAnyOf(array);
        assertEquals(3, pt.getIndex());

        pt.skipToAnyOf(array);
        assertEquals(3, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(array);
        assertEquals(5, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(array);
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testSkipToAnyOf3() {
        assertThrows(IllegalArgumentException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.skipToAnyOf();
        });
    }

    @Test
    public void testSkipToAnyOf4() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        pt.skipToAnyOf(";,");
        assertEquals(3, pt.getIndex());

        pt.skipToAnyOf(";,");
        assertEquals(3, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(";,");
        assertEquals(5, pt.getIndex());

        pt.skip(1);
        pt.skipToAnyOf(";,");
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testSkipToAnyOf5() {
        assertThrows(IllegalArgumentException.class, () -> {
            ParseText pt = new ParseText(str1);
            pt.skipToAnyOf("");
        });
    }

    @Test
    public void testSkipToCS() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        pt.skipTo("c,");
        assertEquals(2, pt.getIndex());

        pt.skipTo(";e");
        assertEquals(5, pt.getIndex());

        pt.skipTo("xyz");
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testMatchSpaces() {
        ParseText pt = new ParseText(str2);
        assertEquals(0, pt.getIndex());
        assertTrue(pt.matchSpaces());
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.matchSpaces());
        assertEquals(3, pt.getIndex());
    }

    @Test
    public void testSkipSpaces() {
        ParseText pt = new ParseText(str2);
        assertEquals(0, pt.getIndex());
        pt.skipSpaces();
        assertEquals(3, pt.getIndex());

        pt.skipSpaces();
        assertEquals(3, pt.getIndex());

        pt.skipToSpace();
        assertEquals(8, pt.getIndex());

        pt.skipSpaces();
        assertTrue(pt.isExhausted());

        pt.reset();
        assertEquals(0, pt.getIndex());
        pt.skipToEnd();
        assertTrue(pt.isExhausted());
    }

    @Test
    public void testMatchNameX() {
        ParseText pt = new ParseText(str1);
        assertEquals(0, pt.getIndex());
        assertTrue(pt.matchName());
        assertEquals(3, pt.getIndex());
        assertEquals(0, pt.getStart());

        assertFalse(pt.matchName());
        assertEquals(3, pt.getIndex());
    }

    @Test
    public void testUnescape() {
        ParseText pt = new ParseText("abc%20def/ghi");
        assertEquals("abc def", pt.unescape(URI.charUnmapper, '/'));
        assertEquals(9, pt.getIndex());
    }

    @Test
    public void testToString() {
        ParseText pt = new ParseText(str1);
        assertEquals("[~^abc,d;e]", pt.toString());
        pt.setIndex(4);
        pt.setStart(2);
        assertEquals("[ab~c,^d;e]", pt.toString());
    }

}
