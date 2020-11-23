package org.fastercode.marmot.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huyaolong
 */
public class DaemonThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public DaemonThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = ("daemon-pool-" + poolNumber.getAndIncrement() + "-thread-");
    }

    public DaemonThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = (prefix + "-daemon-pool-" + poolNumber.getAndIncrement() + "-thread-");
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        if (!t.isDaemon()) {
            t.setDaemon(true);
        }
        return t;
    }

    public static ExecutorService newSingleThreadExecutor(String prefix, int capacity) {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(capacity),
                new DaemonThreadFactory(prefix),
                (r, executor) -> {
                    // do nothing
                }
        );
    }

    public static ExecutorService newFixedThreadPool(String prefix, int capacity, int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(capacity),
                new DaemonThreadFactory(prefix),
                (r, executor) -> {
                    // do nothing
                }
        );
    }

    public static ExecutorService newCachedThreadPool(String prefix, int capacity, int maxPoolSize) {
        return new ThreadPoolExecutor(0, maxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(capacity),
                new DaemonThreadFactory(prefix),
                (r, executor) -> {
                    // do nothing
                }
        );
    }
}
