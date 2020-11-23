package org.fastercode.marmot.core.trigger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * @author huyaolong
 */
@Slf4j
public class MapBufferTrigger<K, E> implements BufferTrigger<K, E> {
    private final AtomicBoolean start = new AtomicBoolean(false);

    private final Map<K, E> buffer;
    private final long maxBufferCount;
    private final BiPredicate<K, E> triggerHandler;
    private final BiConsumer<K, E> rejectHandler;

    private final long period;
    private final TimeUnit periodTimeUnit;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("marmot-trigger-thread-%d")
                    .setDaemon(true)
                    .build()
    );
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public MapBufferTrigger(
            Map<K, E> buffer, long maxBufferCount,
            BiPredicate<K, E> triggerHandler,
            BiConsumer<K, E> rejectHandler,
            long period, TimeUnit periodTimeUnit
    ) {
        this.buffer = buffer;
        this.maxBufferCount = maxBufferCount;
        this.triggerHandler = triggerHandler;
        this.rejectHandler = rejectHandler;
        this.period = period;
        this.periodTimeUnit = periodTimeUnit;
        this.start();
    }

    @Override
    public void enqueue(K key, E element) {
        rwLock.readLock().lock();
        if (buffer.size() >= maxBufferCount && !buffer.containsKey(key)) {
            rwLock.readLock().unlock();
            this.rejectHandler.accept(key, element);
            return;
        }

        try {
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();
            buffer.put(key, element);
        } finally {
            try {
                rwLock.writeLock().unlock();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    public E get(K key) {
        if (key == null || buffer == null || buffer.size() == 0) {
            return null;
        }

        rwLock.readLock().lock();
        try {
            return buffer.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void manuallyTrigger() {
        if (log.isDebugEnabled()) {
            log.debug("trigger start, SIZE=[{}]", buffer.size());
        }

        boolean lock = false;
        try {
            rwLock.writeLock().lock();
            lock = true;
            for (Iterator<K> iterator = buffer.keySet().iterator(); iterator.hasNext(); ) {
                K key = iterator.next();
                E element = buffer.get(key);
                if (log.isDebugEnabled()) {
                    log.debug("trigger loop, KEY=[{}]", key);
                }

                boolean consume = true;
                try {
                    consume = this.triggerHandler.test(key, element);
                } catch (Exception e) {
                    // ignore
                }
                if (consume) {
                    if (log.isDebugEnabled()) {
                        log.debug("trigger consume, KEY=[{}]", key);
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            // ignore
        } finally {
            if (lock) {
                rwLock.writeLock().unlock();
            }
        }
    }

    private void trigger() {
        if (start.get()) {
            try {
                this.manuallyTrigger();
            } catch (Exception e) {
                // ignore
            }
            this.scheduledExecutor.schedule(this::trigger, period, periodTimeUnit);
        }
    }

    @Override
    public long getBufferSize() {
        rwLock.readLock().lock();
        try {
            return buffer.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void clearBuffer() {
        rwLock.writeLock().lock();
        try {
            buffer.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public void start() {
        if (!start.compareAndSet(false, true)) {
            return;
        }
        this.scheduledExecutor.schedule(this::trigger, period, periodTimeUnit);
    }

    @Override
    public void stop() {
        start.compareAndSet(true, false);
    }

}
