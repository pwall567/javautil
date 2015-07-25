/*
 * @(#) IntSequence.java
 */

package net.pwall.util;

/**
 * Generate a sequence of {@code int} numbers.  Useful for generating unique identifiers.
 */
public class IntSequence {

    private int number;

    public IntSequence(int first) {
        number = first - 1;
    }

    public IntSequence() {
        this(0);
    }

    public int next() {
        return ++number;
    }

    public int last() {
        return number;
    }

    public void skip(int n) {
        number += n;
    }

    public void reset(int first) {
        number = first - 1;
    }

    public void reset() {
        reset(0);
    }

}
