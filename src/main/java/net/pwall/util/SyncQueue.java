/*
 * @(#) SyncQueue.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a synchronized first-in first-out queue.  Objects may be added to the end
 * of the queue, and reads will cause the thread to wait until data is available.
 *
 * @author Peter Wall
 */
public class SyncQueue<T> {

    private List<T> queue;

    /**
     * Construct a {@code SyncQueue} with the specified initial capacity.
     *
     * @param initialCapacity  the initial capacity of the queue
     */
    public SyncQueue(int initialCapacity) {
        queue = new ArrayList<>(initialCapacity);
    }

    /**
     * Construct a {@code SyncQueue} with an initial capacity of 10.
     */
    public SyncQueue() {
        this(10);
    }

    /**
     * Add an object to the queue and wake up all threads currently waiting for this queue.
     *
     * @param   object  the object to be added
     */
    public synchronized void add(T object) {
        queue.add(object);
        notifyAll();
    }

    /**
     * Get the next object from the queue, blocking if none is available.  The method will
     * return {@code null} is the thread is interrupted.
     *
     * @return  the next object, or {@code null}
     */
    public synchronized T get() {
        while (queue.size() == 0) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                return null;
            }
        }
        return queue.remove(0);
    }

    /**
     * Determine the current length of the queue.
     *
     * @return  the number of objects currently in the queue
     */
    public synchronized int getSize() {
        return queue.size();
    }

}
