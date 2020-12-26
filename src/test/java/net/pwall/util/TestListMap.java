/*
 * @(#) TestListMap.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2020 Peter Wall
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

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ListMap}.
 *
 * @author Peter Wall
 */
public class TestListMap {

    @Test
    public void testPutSingle() {
        ListMap<String, Integer> map = new ListMap<>();
        assertEquals(0, map.size());
        map.put("abcde", 1234);
        assertEquals(1, map.size());
        assertEquals(1234, map.get("abcde"));
    }

    @Test
    public void testContains() {
        ListMap<String, Integer> map = new ListMap<>();
        assertEquals(0, map.size());
        map.put("abcde", 1234);
        assertTrue(map.containsKey("abcde"));
        assertTrue(map.containsValue(1234));
    }

    @Test
    public void testPutMultiple() {
        ListMap<String, Integer> map = new ListMap<>();
        map.put("abcde", 1234);
        map.put("pqrst", 8888);
        map.put("fghij", 999);
        assertEquals(3, map.size());
        Iterator<String> keys = map.keySet().iterator();
        assertTrue(keys.hasNext());
        String key = keys.next();
        assertEquals("abcde", key);
        assertEquals(1234, map.get(key));
        assertTrue(keys.hasNext());
        key = keys.next();
        assertEquals("pqrst", key);
        assertEquals(8888, map.get(key));
        assertTrue(keys.hasNext());
        key = keys.next();
        assertEquals("fghij", key);
        assertEquals(999, map.get(key));
        assertFalse(keys.hasNext());
    }

    @Test
    public void testToString() {
        ListMap<String, Integer> map = new ListMap<>();
        map.put("abcde", 1234);
        map.put("pqrst", 8888);
        map.put("fghij", 999);
        assertEquals("{abcde=1234, pqrst=8888, fghij=999}", map.toString());
    }

    @Test
    public void testToStringWithSelfReference() {
        ListMap<String, Object> map = new ListMap<>();
        @SuppressWarnings("UnnecessaryLocalVariable") ListMap<String, Object> map2 = map;
        map.put("abcde", "first");
        map.put("pqrst", map2);
        assertEquals("{abcde=first, pqrst=(this Map)}", map.toString());
    }

}
