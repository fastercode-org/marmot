package org.fastercode.marmot.core.trigger;

/**
 * @author huyaolong
 */
public interface BufferTrigger<K, E> {
    void enqueue(K key, E element);

    E get(K key);

    void manuallyTrigger();

    long getBufferSize();

    void clearBuffer();

    void start();

    void stop();
}
