/*
 * @(#) UserError.java
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

/**
 * A class to represent errors in user input - for example, command-line arguments incorrectly
 * specified.  This class of error differs from general exceptions in that the stack trace
 * information is never required, and a nested exception is irrelevant.  The class is derived
 * from {@link RuntimeException} so that it can be thrown from locations that would not allow a
 * checked exception (like the {@link Runnable#run() run()} method of the {@link Runnable}
 * interface).
 *
 * @author Peter Wall
 */
public class UserError extends RuntimeException {

    private static final long serialVersionUID = -7953470627573715294L;

    public static final String formatKey = UserError.class.getName() + ".format";
    public static final String defaultFormat = "Error: {0}";

    /**
     * Construct a {@code UserError} with the supplied error message.
     *
     * @param   msg     the error message
     */
    public UserError(String msg) {
        super(msg);
    }

    /**
     * Print the stack trace for this error.  This method overrides the default behaviour to
     * output only the error line and <strong>not</strong> the stack trace.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Print the stack trace for this error to a specified {@link PrintStream}.  This method
     * overrides the default behaviour to output only the error line and <strong>not</strong>
     * the stack trace.
     *
     * @param   ps      the {@link PrintStream}
     */
    @Override
    public void printStackTrace(PrintStream ps) {
        ps.println(this);
    }

    /**
     * Print the stack trace for this error to a specified {@link PrintWriter}.  This method
     * overrides the default behaviour to output only the error line and <strong>not</strong>
     * the stack trace.
     *
     * @param   pw      the {@link PrintWriter}
     */
    @Override
    public void printStackTrace(PrintWriter pw) {
        pw.println(this);
    }

    /**
     * Create a {@link String} representation of the {@code UserError}.  This method uses a
     * system property to determine the display format of the error.
     *
     * @return  the string representation of the error
     */
    @Override
    public String toString() {
        String pattern = System.getProperty(formatKey, defaultFormat);
        return MessageFormat.format(pattern, getLocalizedMessage());
    }

}
