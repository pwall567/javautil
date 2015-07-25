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
    public void test_toIdentifier() {
        assertEquals("A", Strings.toIdentifier(0));
        assertEquals("B", Strings.toIdentifier(1));
        assertEquals("Z", Strings.toIdentifier(25));
        assertEquals("AA", Strings.toIdentifier(26));
        assertEquals("AB", Strings.toIdentifier(27));
    }

}
