package org.fastercode.marmot.monitor.log.logback.kafka;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.FailedDeliveryCallback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huyaolong
 */
public class KafkaAppender<E> extends KafkaAppenderConfig<E> {
    private static final String LOGGER_PREFIX = KafkaAppender.class.getPackage().getName();
    private static final String KAFKA_LOGGER_PREFIX = KafkaProducer.class.getPackage().getName().replaceFirst("\\.producer$", "");

    private final AtomicBoolean start = new AtomicBoolean(false);
    protected final ReentrantLock lock = new ReentrantLock(false);

    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
        @Override
        public void onFailedDelivery(E evt, Throwable throwable) {
            aai.appendLoopOnAppenders(evt);
        }
    };

    public KafkaAppender() {
        // setting these as config values sidesteps an unnecessary warning (minor bug in KafkaProducer)
        addProducerConfigValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        addProducerConfigValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
    }

    @Override
    public void doAppend(E e) {
        if (e instanceof ILoggingEvent && ((ILoggingEvent) e).getLoggerName().startsWith(LOGGER_PREFIX))
            return;
        if (e instanceof ILoggingEvent && ((ILoggingEvent) e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX))
            return;
        super.doAppend(e);
    }

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

    /**
     * Lazy initializer for producer, patterned after commons-lang.
     *
     * @see <a href="https://commons.apache.org/proper/commons-lang/javadocs/api-3.4/org/apache/commons/lang3/concurrent/LazyInitializer.html">LazyInitializer</a>
     */
    private class LazyProducer {

        private volatile Producer<byte[], byte[]> producer;

        public Producer<byte[], byte[]> get() {
            Producer<byte[], byte[]> result = this.producer;
            if (result == null) {
                synchronized (this) {
                    result = this.producer;
                    if (result == null) {
                        this.producer = result = this.initialize();
                    }
                }
            }

            return result;
        }

        protected Producer<byte[], byte[]> initialize() {
            Producer<byte[], byte[]> producer = null;
            try {
                producer = createProducer();
            } catch (Exception e) {
                addError("error creating producer", e);
            }
            return producer;
        }

        public boolean isInitialized() {
            return producer != null;
        }
    }

    protected Producer<byte[], byte[]> createProducer() {
        return new KafkaProducer<>(new HashMap<>(producerConfig));
    }

}
