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

}
