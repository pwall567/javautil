/*
 * @(#) SyncQueue.java
 *
 * javautil Java Utility Library
 * Copyright (c) 2013, 2014, 2015 Peter Wall
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
 * or inserted at the beginning of the queue, and reads will cause the thread to wait until data
 * is available.  An optional maximum capacity may be specified; in that case the thread trying
 * to add to the queue will block until space is available.
 *
 * @author Peter Wall
 */
public class SyncQueue<T> {

    public static final int defaultInitialCapacity = 10;
    public static final int defaultMaxLength = 0; // indicates no limit

    private List<T> queue;
    private int maxLength;

    /**
     * Construct a {@code SyncQueue} with the specified initial capacity and maximum size.
     *
     * @param   initialCapacity      the initial capacity of the queue
     * @param   maxLength            the maximum length of the queue
     */
    public SyncQueue(int initialCapacity, int maxLength) {
        queue = new ArrayList<>(initialCapacity);
        this.maxLength = maxLength;
    }

    /**
     * Construct a {@code SyncQueue} with the specified initial capacity.
     *
     * @param   initialCapacity      the initial capacity of the queue
     */
    public SyncQueue(int initialCapacity) {
        this(initialCapacity, defaultMaxLength);
    }

    /**
     * Construct a {@code SyncQueue} with the default initial capacity.
     */
    public SyncQueue() {
        this(defaultInitialCapacity);
    }

    /**
     * Add an object to the end of the queue and wake up all threads currently waiting for this
     * queue.
     *
     * @param   object  the object to be added
     * @return  {@code true} if the operation was successful
     */
    public synchronized boolean add(T object) {
        if (!checkCapacity())
            return false;
        queue.add(object);
        notifyAll();
        return true;
    }

    /**
     * Add an object to the end of the queue and wake up all threads currently waiting for this
     * queue.  If there is already an object in the queue identical to the one supplied, don't
     * add the new one.
     *
     * @param   object  the object to be added
     * @return  {@code true} if the operation was successful
     */
    public synchronized boolean addUnique(T object) {
        if (queue.contains(object))
            return true;
        if (!checkCapacity())
            return false;
        queue.add(object);
        notifyAll();
        return true;
    }

    /**
     * Insert an object at the beginning of the queue and wake up all threads currently waiting
     * for this queue.
     *
     * @param   object  the object to be added
     * @return  {@code true} if the operation was successful
     */
    public synchronized boolean insert(T object) {
        if (!checkCapacity())
            return false;
        queue.add(0, object);
        notifyAll();
        return true;
    }

    /**
     * Insert an object at the beginning of the queue and wake up all threads currently waiting
     * for this queue.  If there is already an object in the queue identical to the one
     * supplied, delete it first and insert the new one at the start.
     *
     * @param   object  the object to be added
     * @return  {@code true} if the operation was successful
     */
    public synchronized boolean insertUnique(T object) {
        queue.remove(object);
        if (!checkCapacity())
            return false;
        queue.add(0, object);
        notifyAll();
        return true;
    }

    /**
     * Wait for the queue to have capacity to take another entry.
     *
     * @return  {@code true} if the queue has capacity; {@code false} if the thread was
     *          interrupted while waiting for capacity
     */
    private boolean checkCapacity() {
        if (maxLength > 0) {
            while (queue.size() >= maxLength) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Remove the specified object from the queue.
     *
     * @param   object  the object to be removed
     * @return  {@code true} if the object was removed
     */
    public synchronized boolean remove(T object) {
        if (!queue.remove(object))
            return false;
        notifyAll();
        return true;
    }

    /**
     * Get the next object from the queue, blocking if none is available.  The method will
     * return {@code null} if the thread is interrupted.
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
        T result = queue.remove(0);
        notifyAll();
        return result;
    }

    /**
     * Determine the current length of the queue.
     *
     * @return  the number of objects currently in the queue
     */
    public synchronized int getSize() {
        return queue.size();
    }

    /**
     * Get the maximum queue length.
     *
     * @return  the maximum queue length
     */
    public synchronized int getMaxLength() {
        return maxLength;
    }

    /**
     * Get the maximum queue length.
     *
     * @param   maxLength   the maximum queue length
     */
    public synchronized void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

}
