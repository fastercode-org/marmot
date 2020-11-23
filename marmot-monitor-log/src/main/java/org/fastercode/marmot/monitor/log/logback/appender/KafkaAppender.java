package org.fastercode.marmot.monitor.log.logback.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huyaolong
 */
public class KafkaAppender<E> extends UnsynchronizedAppenderBase<E> {
    protected final ReentrantLock lock = new ReentrantLock(false);

    @Override
    protected void append(E e) {

    }

    @Override
    public void start() {
        int errors = 0;
        if (errors == 0) {
            super.start();
        }
    }

    @Override
    public void stop() {
        this.lock.lock();

        try {
            super.stop();
        } finally {
            this.lock.unlock();
        }
    }
}
