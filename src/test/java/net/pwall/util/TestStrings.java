package net.pwall.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

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
