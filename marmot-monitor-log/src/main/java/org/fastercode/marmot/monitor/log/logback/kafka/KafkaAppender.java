package org.fastercode.marmot.monitor.log.logback.kafka;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huyaolong
 */
public class KafkaAppender<E> extends KafkaAppenderConfig<E> {
    private final AtomicBoolean start = new AtomicBoolean(false);
    protected final ReentrantLock lock = new ReentrantLock(false);

    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

    @Override
    protected void append(E e) {

    }

    @Override
    public void start() {
        if (!start.compareAndSet(false, true)) {
            return;
        }

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
            start.compareAndSet(true, false);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void addAppender(Appender<E> newAppender) {
        aai.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        return aai.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        return aai.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
}
