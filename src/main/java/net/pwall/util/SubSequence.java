/*
 * @(#) SubSequence.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014 Peter Wall
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

/**
 * A sub-sequence of a {@link CharSequence}.
 *
 * @author Peter Wall
 */

public class SubSequence implements CharSequence {

    private CharSequence seq;
    private int start;
    private int end;
    private String string;

    public SubSequence(CharSequence seq, int start, int end) {
        if (seq == null)
            throw new IllegalArgumentException("SubSequence parent may not be null");
        if (start < 0 || start > seq.length())
            throw new IllegalArgumentException("SubSequence start incorrect");
        if (end < start || end > seq.length())
            throw new IllegalArgumentException("SubSequence end incorrect");
        this.seq = seq;
        this.start = start;
        this.end = end;
        string = null;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        int i = index + start;
        if (i >= end)
            throw new StringIndexOutOfBoundsException("Subsequence index incorrect");
        return seq.charAt(i);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int len = length();
        if (start < 0 || start > len)
            throw new StringIndexOutOfBoundsException("Subsequence start index incorrect");
        if (end < start || end > len)
            throw new StringIndexOutOfBoundsException("Subsequence end index incorrect");
        return new SubSequence(seq, start + this.start, end + this.start);
    }

    @Override
    public String toString() {
        // note - this is not synchronized, so in rare cases it may be performed twice;
        // the code is idempotent so this will not cause errors unless the underlying
        // character sequence changes
        if (string == null) {
            int len = length();
            if (len == 0)
                string = "";
            else {
                char[] array = new char[len];
                for (int i = 0; i < len; i++)
                    array[i] = seq.charAt(i + start);
                string = new String(array);
            }
        }
        return string;
    }

}
