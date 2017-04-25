/*
 * @(#) MultiCharMapper.java
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
 * An implementation of {@link CharMapper} that delegates to a number of other mappers in turn.
 *
 * @author Peter Wall
 */
public class MultiCharMapper implements CharMapper {

    private CharMapper[] mappers;

    /**
     * Construct a {@code MultiCharMapper} with an empty list of delegate mappers.
     */
    public MultiCharMapper() {
        mappers = null;
    }

    /**
     * Construct a {@code MultiCharMapper} with the supplied list of delegate mappers.
     *
     * @param mappers   the delegate {@link CharMapper} instances
     */
    public MultiCharMapper(CharMapper ... mappers) {
        this.mappers = mappers;
    }

    /**
     * Add a {@link CharMapper} instance to the list of delegate mappers.
     *
     * @param mapper    the new {@link CharMapper}
     */
    public void addCharMapper(CharMapper mapper) {
        if (mappers == null)
            mappers = new CharMapper[] { mapper };
        else {
            int n = mappers.length;
            CharMapper[] newArray = new CharMapper[n + 1];
            System.arraycopy(mappers, 0, newArray, 0, n);
            newArray[n] = mapper;
            mappers = newArray;
        }
    }

    /**
     * Map character to it's "escaped" string equivalent.  Try each of the supplied
     * {@link CharMapper} instances in turn, and if any matches, return that string.  Return
     * {@code null} if none match.
     *
     * @param codePoint the Unicode code point of the character to be mapped
     * @return          the escaped string equivalent, or {@code null} if no escape needed
     */
    @Override
    public String map(int codePoint) {
        if (mappers != null) {
            for (CharMapper mapper : mappers) {
                String result = mapper.map(codePoint);
                if (result != null)
                    return result;
            }
        }
        return null;
    }

}
