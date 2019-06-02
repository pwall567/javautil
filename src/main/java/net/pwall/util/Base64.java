/*
 * @(#) Base64.java
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

/**
 * Encode and decode Base64.  This functionality exists in a variety of other libraries, but
 * those libraries generally include a lot of other unneeded code.
 *
 * @author  Peter Wall
 */
public class Base64 {

    private static final byte[] base64Bytes = new byte[64];
    private static final byte[] base64URLBytes = new byte[64];
    private static final byte[] reverseBytes = new byte[128];
    private static final byte[] emptyBytes = new byte[0];

    static {
        for (int i = 0; i < 128; i++)
            reverseBytes[i] = (byte)0xFF;

        for (int i = 0; i < 26; i++) {
            base64Bytes[i] = (byte)('A' + i);
            base64URLBytes[i] = (byte)('A' + i);
            reverseBytes['A' + i] = (byte)i;
        }

        for (int i = 0; i < 26; i++) {
            base64Bytes[i + 26] = (byte)('a' + i);
            base64URLBytes[i + 26] = (byte)('a' + i);
            reverseBytes['a' + i] = (byte)(i + 26);
        }

        for (int i = 0; i < 10; i++) {
            base64Bytes[i + 52] = (byte)('0' + i);
            base64URLBytes[i + 52] = (byte)('0' + i);
            reverseBytes['0' + i] = (byte)(i + 52);
        }

        base64Bytes[62] = (byte)'+';
        reverseBytes['+'] = (byte)62;
        base64URLBytes[62] = (byte)'-';
        reverseBytes['-'] = (byte)62;

        base64Bytes[63] = (byte)'/';
        reverseBytes['/'] = (byte)63;
        base64URLBytes[63] = (byte)'_';
        reverseBytes['_'] = (byte)63;
    }

    /**
     * Private constructor - do not instantiate.
     */
    private Base64() {
    }

    /**
     * Get the encoded length of data encoded into Base64.
     *
     * @param   n       the source data length
     * @return          the encoded data length
     * @throws  NullPointerException if the data is null
     */
    public static int getEncodedLength(int n) {
        return (n + 2) / 3 * 4;
    }

    /**
     * Get the encoded length of data encoded into the URL variant of Base64.  This variant uses
     * different characters for the last two positions in the encoding table, and (significantly
     * for length calculation) it doesn't pad with equal signs.
     *
     * @param   n       the source data length
     * @return          the encoded data length
     * @throws  NullPointerException if the data is null
     */
    public static int getEncodedURLLength(int n) {
        return (n * 4 + 2) / 3;
    }

    /**
     * Encode a byte array into Base64, returning another byte array.
     *
     * @param   data    the source data
     * @return          the encoded data
     * @throws  NullPointerException if the data is null
     */
    public static byte[] encode(byte[] data) {
        int n = data.length;
        if (n == 0)
            return emptyBytes;
        byte[] bytes = new byte[getEncodedLength(n)];
        int i = 0;
        int x = 0;
        int a, b, c;
        int nm3 = n - 3;
        while (i <= nm3) {
            a = data[i++];
            b = data[i++];
            c = data[i++];
            bytes[x++] = base64Bytes[(a >> 2) & 0x3F];
            bytes[x++] = base64Bytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)];
            bytes[x++] = base64Bytes[((b << 2) & 0x3C) | ((c >> 6) & 0x03)];
            bytes[x++] = base64Bytes[c & 0x3F];
        }
        n -= i;
        if (n > 0) {
            a = data[i];
            bytes[x++] = base64Bytes[(a >> 2) & 0x3F];
            if (n == 1) {
                bytes[x++] = base64Bytes[(a << 4) & 0x30];
                bytes[x++] = '=';
                bytes[x] = '=';
            }
            else {
                b = data[i + 1];
                bytes[x++] = base64Bytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)];
                bytes[x++] = base64Bytes[(b << 2) & 0x3C];
                bytes[x] = '=';
            }
        }
        return bytes;
    }

    /**
     * Encode a byte array into the URL variant of Base64, returning another byte array.  The
     * URL variant uses different characters for the last two positions in the encoding table,
     * and it doesn't pad with equal signs.
     *
     * @param   data    the source data
     * @return          the encoded data
     * @throws  NullPointerException if the data is null
     */
    public static byte[] encodeURL(byte[] data) {
        int n = data.length;
        if (n == 0)
            return emptyBytes;
        byte[] bytes = new byte[getEncodedURLLength(n)];
        int i = 0;
        int x = 0;
        int a, b, c;
        int nm3 = n - 3;
        while (i <= nm3) {
            a = data[i++];
            b = data[i++];
            c = data[i++];
            bytes[x++] = base64URLBytes[(a >> 2) & 0x3F];
            bytes[x++] = base64URLBytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)];
            bytes[x++] = base64URLBytes[((b << 2) & 0x3C) | ((c >> 6) & 0x03)];
            bytes[x++] = base64URLBytes[c & 0x3F];
        }
        n -= i;
        if (n > 0) {
            a = data[i];
            bytes[x++] = base64URLBytes[(a >> 2) & 0x3F];
            if (n == 1)
                bytes[x++] = base64URLBytes[(a << 4) & 0x30];
            else {
                b = data[i + 1];
                bytes[x++] = base64URLBytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)];
                bytes[x++] = base64URLBytes[(b << 2) & 0x3C];
            }
        }
        return bytes;
    }

    /**
     * Append a byte array to an {@link Appendable} as Base64-encoded characters.
     *
     * @param   app     the {@link Appendable}
     * @param   data    the source data
     * @throws  IOException if thrown by the {@link Appendable}
     * @throws  NullPointerException if the data is null
     */
    public static void appendEncoded(Appendable app, byte[] data) throws IOException {
        appendEncoded(app, data, 0, data.length);
    }

    /**
     * Append a section of a byte array to an {@link Appendable} as Base64-encoded characters.
     *
     * @param   app     the {@link Appendable}
     * @param   data    the source data
     * @param   start   the start index of the data to be encoded
     * @param   end     the end index
     * @throws  IOException if thrown by the {@link Appendable}
     * @throws  NullPointerException if the data is null
     */
    public static void appendEncoded(Appendable app, byte[] data, int start, int end)
            throws IOException {
        int i = start;
        int a, b, c;
        int endm3 = end - 3;
        while (i <= endm3) {
            a = data[i++];
            b = data[i++];
            c = data[i++];
            app.append((char)(base64Bytes[(a >> 2) & 0x3F])).
                    append((char)(base64Bytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)])).
                    append((char)(base64Bytes[((b << 2) & 0x3C) | ((c >> 6) & 0x03)])).
                    append((char)(base64Bytes[c & 0x3F]));
        }
        int n = end - i;
        if (n > 0) {
            a = data[i];
            app.append((char)(base64Bytes[(a >> 2) & 0x3F]));
            if (n == 1)
                app.append((char)(base64Bytes[(a << 4) & 0x30])).append('=').append('=');
            else {
                b = data[i + 1];
                app.append((char)(base64Bytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)])).
                        append((char)(base64Bytes[(b << 2) & 0x3C])).append('=');
            }
        }
    }

    /**
     * Append a byte array to an {@link Appendable} as Base64-encoded characters, using the URL
     * variant of Base64.
     *
     * @param   app     the {@link Appendable}
     * @param   data    the source data
     * @throws  IOException if thrown by the {@link Appendable}
     * @throws  NullPointerException if the data is null
     */
    public static void appendURLEncoded(Appendable app, byte[] data) throws IOException {
        appendURLEncoded(app, data, 0, data.length);
    }

    /**
     * Append a section of a byte array to an {@link Appendable} as Base64-encoded characters,
     * using the URL variant of Base64.
     *
     * @param   app     the {@link Appendable}
     * @param   data    the source data
     * @param   start   the start index of the data to be encoded
     * @param   end     the end index
     * @throws  IOException if thrown by the {@link Appendable}
     * @throws  NullPointerException if the data is null
     */
    public static void appendURLEncoded(Appendable app, byte[] data, int start, int end)
            throws IOException {
        int i = start;
        int a, b, c;
        int endm3 = end - 3;
        while (i <= endm3) {
            a = data[i++];
            b = data[i++];
            c = data[i++];
            app.append((char)(base64URLBytes[(a >> 2) & 0x3F])).
                    append((char)(base64URLBytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)])).
                    append((char)(base64URLBytes[((b << 2) & 0x3C) | ((c >> 6) & 0x03)])).
                    append((char)(base64URLBytes[c & 0x3F]));
        }
        int n = end - i;
        if (n > 0) {
            a = data[i];
            app.append((char)(base64URLBytes[(a >> 2) & 0x3F]));
            if (n == 1)
                app.append((char)(base64URLBytes[(a << 4) & 0x30]));
            else {
                b = data[i + 1];
                app.append((char)(base64URLBytes[((a << 4) & 0x30) | ((b >> 4) & 0x0F)])).
                        append((char)(base64URLBytes[(b << 2) & 0x3C]));
            }
        }
    }

    /**
     * Encode a byte array into Base64, returning a string.
     *
     * @param   data    the source data
     * @return          the encoded string
     * @throws  NullPointerException if the data is null
     */
    public String encodeString(byte[] data) {
        StringBuilder sb = new StringBuilder(getEncodedLength(data.length));
        try {
            appendEncoded(sb, data);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    /**
     * Encode a byte array into the URL variant of Base64, returning a string.  The URL variant
     * uses different characters for the last two positions in the encoding table, and it
     * doesn't pad with equal signs.
     *
     * @param   data    the source data
     * @return          the encoded data
     * @throws  NullPointerException if the data is null
     */
    public String encodeStringURL(byte[] data) {
        StringBuilder sb = new StringBuilder(getEncodedURLLength(data.length));
        try {
            appendURLEncoded(sb, data);
        }
        catch (IOException e) {
            // can't happen - StringBuilder.append() does not throw IOException
        }
        return sb.toString();
    }

    /**
     * Decode a byte array from Base64.  Both the original and the URL variants are handled.
     *
     * @param   data    the source data
     * @return          the decoded data
     * @throws  IllegalArgumentException if the data is not valid Base64
     * @throws  NullPointerException if the data is null
     */
    public static byte[] decode(byte[] data) {
        int n = data.length;
        if (n == 0)
            return emptyBytes;
        if ((n & 3) == 0) { // length divisible by 4 - could have trailing = sign(s)
            if (data[n - 1] == '='){
                n--;
                if (data[n - 1] == '=')
                    n--;
            }
        }
        else { // otherwise length can't be 1, 5, 9, 13 ...
            if ((n & 3) == 1)
                throw new IllegalArgumentException("Incorrect number of bytes for Base64");
        }
        byte[] bytes = new byte[(n * 3) >> 2];
        int x = 0;
        int i = 0;
        int a, b, c, d;
        int nm4 = n - 4;
        while (i <= nm4) {
            a = decodeByte(data[i++]);
            b = decodeByte(data[i++]);
            c = decodeByte(data[i++]);
            d = decodeByte(data[i++]);
            bytes[x++] = (byte)(((a << 2) & 0xFC) | ((b >> 4) & 0x03));
            bytes[x++] = (byte)(((b << 4) & 0xF0) | ((c >> 2) & 0xF));
            bytes[x++] = (byte)(((c << 6) & 0xC0) | d);
        }
        n -= i;
        if (n > 0) {
            a = decodeByte(data[i]);
            b = decodeByte(data[i + 1]);
            bytes[x++] = (byte)(((a << 2) & 0xFC) | ((b >> 4) & 0x03));
            if (n == 2) {
                if ((b & 0xF) != 0)
                    throw new IllegalArgumentException("Illegal character in Base64");
            }
            else {
                c = decodeByte(data[i + 2]);
                bytes[x++] = (byte)(((b << 4) & 0xF0) | ((c >> 2) & 0xF));
                if ((c & 3) != 0)
                    throw new IllegalArgumentException("Illegal character in Base64");
            }
        }
        return bytes;
    }

    /**
     * Decode a {@link CharSequence} ({@link String}, {@link StringBuilder} etc.) from Base64.
     * Both the original and the URL variants are handled.
     *
     * @param   data    the source data
     * @return          the decoded data
     * @throws  IllegalArgumentException if the data is not valid Base64
     * @throws  NullPointerException if the data is null
     */
    public static byte[] decode(CharSequence data) {
        int n = data.length();
        if (n == 0)
            return emptyBytes;
        if ((n & 3) == 0) { // length divisible by 4 - could have trailing = sign(s)
            if (data.charAt(n - 1) == '='){
                n--;
                if (data.charAt(n - 1) == '=')
                    n--;
            }
        }
        else { // otherwise length can't be 1, 5, 9, 13 ...
            if ((n & 3) == 1)
                throw new IllegalArgumentException("Incorrect number of bytes for Base64");
        }
        byte[] bytes = new byte[(n * 3) >> 2];
        int x = 0;
        int i = 0;
        int a, b, c, d;
        int nm4 = n - 4;
        while (i <= nm4) {
            a = decodeByte(data.charAt(i++));
            b = decodeByte(data.charAt(i++));
            c = decodeByte(data.charAt(i++));
            d = decodeByte(data.charAt(i++));
            bytes[x++] = (byte)(((a << 2) & 0xFC) | ((b >> 4) & 0x03));
            bytes[x++] = (byte)(((b << 4) & 0xF0) | ((c >> 2) & 0xF));
            bytes[x++] = (byte)(((c << 6) & 0xC0) | d);
        }
        n -= i;
        if (n > 0) {
            a = decodeByte(data.charAt(i));
            b = decodeByte(data.charAt(i + 1));
            bytes[x++] = (byte)(((a << 2) & 0xFC) | ((b >> 4) & 0x03));
            if (n == 2) {
                if ((b & 0xF) != 0)
                    throw new IllegalArgumentException("Illegal character in Base64");
            }
            else {
                c = decodeByte(data.charAt(i + 2));
                bytes[x++] = (byte)(((b << 4) & 0xF0) | ((c >> 2) & 0xF));
                if ((c & 3) != 0)
                    throw new IllegalArgumentException("Illegal character in Base64");
            }
        }
        return bytes;
    }

    private static int decodeByte(int b) {
        if ((b & ~0x7F) == 0) {
            byte result = reverseBytes[b];
            if ((result & ~0x3F) == 0)
                return result;
        }
        throw new IllegalArgumentException("Illegal character in Base64");
    }

}
