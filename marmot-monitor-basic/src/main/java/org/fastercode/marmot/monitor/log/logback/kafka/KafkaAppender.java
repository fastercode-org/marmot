package org.fastercode.marmot.monitor.log.logback.kafka;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.AsynchronousDeliveryStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.FailedDeliveryCallback;
import org.fastercode.marmot.monitor.log.logback.kafka.keying.NoKeyKeyingStrategy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaAppender<E> extends KafkaAppenderConfig<E> {
    private static final String LOGGER_PREFIX = KafkaAppender.class.getPackage().getName();
    private static final String KAFKA_LOGGER_PREFIX = KafkaProducer.class.getPackage().getName().replaceFirst("\\.producer$", "");
    private final AtomicBoolean start = new AtomicBoolean(false);

    @Getter(AccessLevel.PROTECTED)
    private LazyProducer lazyProducer = null;

    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
        @Override
        public void onFailedDelivery(E evt, Throwable throwable) {
            aai.appendLoopOnAppenders(evt);
        }
    };

    public KafkaAppender() {
        // https://kafka.apache.org/documentation.html#newproducerconfigs
        addProducerConfigValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        addProducerConfigValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        addProducerConfigValue(ProducerConfig.ACKS_CONFIG, "0");
        addProducerConfigValue(ProducerConfig.RETRIES_CONFIG, "0");
        addProducerConfigValue(ProducerConfig.LINGER_MS_CONFIG, "1000");
        addProducerConfigValue(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "3000");

        addProducerConfigValue(ProducerConfig.MAX_BLOCK_MS_CONFIG, "0");
        addProducerConfigValue(ProducerConfig.METADATA_MAX_AGE_CONFIG, "300000");
        addProducerConfigValue(ProducerConfig.METADATA_MAX_IDLE_CONFIG, "300000");

        this.setDeliveryStrategy(new AsynchronousDeliveryStrategy());
        this.setKeyingStrategy(new NoKeyKeyingStrategy());
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
        final byte[] key = keyingStrategy.createKey(e);
        final byte[] value = encoder.encode(e);
        final Long timestamp = isAppendTimestamp() ? getTimestamp(e) : null;

        final ProducerRecord<byte[], byte[]> msg = new ProducerRecord<>(topic, partition, timestamp, key, value);

        final Producer<byte[], byte[]> producer = lazyProducer.get();
        if (producer != null) {
            deliveryStrategy.send(producer, msg, e, failedDeliveryCallback);
        } else {
            failedDeliveryCallback.onFailedDelivery(e, null);
        }
    }

    protected Long getTimestamp(E e) {
        if (e instanceof ILoggingEvent) {
            return ((ILoggingEvent) e).getTimeStamp();
        } else {
            return System.currentTimeMillis();
        }
    }

    @Override
    public void start() {
        if (!checkPrerequisites()) return;

        if (!start.compareAndSet(false, true)) {
            return;
        }

        if (partition != null && partition < 0) {
            partition = null;
        }

        lazyProducer = new LazyProducer();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (lazyProducer != null && lazyProducer.isInitialized()) {
            try {
                lazyProducer.get().close();
            } catch (KafkaException e) {
                this.addWarn("Failed to shut down kafka producer: " + e.getMessage(), e);
            }
            lazyProducer = null;
            start.compareAndSet(true, false);
        }
    }

    /**
     * Lazy initializer for producer, patterned after commons-lang.
     *
     * @see <a href="https://commons.apache.org/proper/commons-lang/javadocs/api-3.4/org/apache/commons/lang3/concurrent/LazyInitializer.html">LazyInitializer</a>
     */
    protected class LazyProducer {

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
