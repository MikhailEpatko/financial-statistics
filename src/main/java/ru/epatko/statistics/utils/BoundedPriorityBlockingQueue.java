package ru.epatko.statistics.utils;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BoundedPriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {

    private static final long serialVersionUID = 5595510919245408276L;

    private final int capacity;
    private PriorityBlockingQueue<E> queue;

    public BoundedPriorityBlockingQueue(int initialCapacity) {
        queue = new PriorityBlockingQueue<>(initialCapacity);
        this.capacity = initialCapacity;
    }

    @Override
    public void put(E e) {
        queue.put(e);
        if (size() > capacity) {
            Object[] array = queue.toArray();
            Arrays.sort(array);
            queue.remove(array[array.length - 1]);
        }
    }

    @Override
    public boolean add(E e) {
        boolean result = queue.add(e);
        if (size() > capacity) {
            Object[] array = queue.toArray();
            Arrays.sort(array);
            queue.remove(array[array.length - 1]);
        }
        return result;
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public E take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E peek() {
        return queue.peek();
    }
}
