package org.fastercode.marmot.monitor.log.logback.kafka;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.ErrorStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.AsynchronousDeliveryStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.DeliveryStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.FailedDeliveryCallback;
import org.fastercode.marmot.monitor.log.logback.kafka.keying.KeyingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Slf4j
public class KafkaAppenderTest {
    private static final Logger logKafka = LoggerFactory.getLogger("SEND-TO-KAFKA");

    private final KafkaAppender<ILoggingEvent> appender = new KafkaAppender<>();
    private final LoggerContext ctx = new LoggerContext();
    @SuppressWarnings("unchecked")
    private final Encoder<ILoggingEvent> encoder = mock(Encoder.class);
    private final KeyingStrategy<ILoggingEvent> keyingStrategy = mock(KeyingStrategy.class);
    @SuppressWarnings("unchecked")
    private final DeliveryStrategy deliveryStrategy = mock(DeliveryStrategy.class);

    @Before
    public void before() {
        ctx.setName("testctx");
        ctx.setStatusManager(new BasicStatusManager());
        appender.setContext(ctx);
        appender.setName("kafkaAppenderBase");
        appender.setEncoder(encoder);
        appender.setTopic("topic");
        appender.addProducerConfig("bootstrap.servers=vm-kafka:9092");
        appender.setKeyingStrategy(keyingStrategy);
        appender.setDeliveryStrategy(deliveryStrategy);
        ctx.start();
    }

    @After
    public void after() {
        ctx.stop();
        appender.stop();
    }

    @Test
    @Ignore
    @SneakyThrows
    public void testLogSend() {
        for (int i = 0; i < 10000; i++) {
            logKafka.info("test-{}", i);
            if (i < 10) {
                Thread.sleep(500);
            }
        }
        Thread.sleep(10000L);
    }

    @Test
    public void testSend() {
        appender.setTopic("test-topic");
        appender.start();
        byte[] key = null;
        byte[] value = "test".getBytes();
        final ProducerRecord<byte[], byte[]> msg = new ProducerRecord<>("test-topic", null, null, key, value);
        Producer<byte[], byte[]> producer = appender.getLazyProducer().get();
        appender.setDeliveryStrategy(new AsynchronousDeliveryStrategy());
        DeliveryStrategy deliveryStrategy = appender.getDeliveryStrategy();
        for (int i = 0; i < 1000; i++) {
            deliveryStrategy.send(producer, msg, null, new FailedDeliveryCallback<Object>() {
                @Override
                public void onFailedDelivery(Object evt, Throwable throwable) {
                    log.warn("\ntest - warn: {}", throwable.getMessage());
                }
            });
        }
    }

    @Test
    public void testPerfectStartAndStop() {
        appender.start();
        assertTrue("isStarted", appender.isStarted());
        appender.stop();
        assertFalse("isStopped", appender.isStarted());
        assertThat(ctx.getStatusManager().getCopyOfStatusList(), empty());
        verifyZeroInteractions(encoder, keyingStrategy, deliveryStrategy);
    }

    @Test
    public void testDontStartWithoutTopic() {
        appender.setTopic(null);
        appender.start();
        assertFalse("isStarted", appender.isStarted());
        assertThat(ctx.getStatusManager().getCopyOfStatusList(),
                hasItem(new ErrorStatus("No topic set for the appender named [\"kafkaAppenderBase\"].", null)));
    }

    @Test
    public void testDontStartWithoutBootstrapServers() {
        appender.getProducerConfig().clear();
        appender.start();
        assertFalse("isStarted", appender.isStarted());
        assertThat(ctx.getStatusManager().getCopyOfStatusList(),
                hasItem(new ErrorStatus("No \"bootstrap.servers\" set for the appender named [\"kafkaAppenderBase\"].", null)));
    }

    @Test
    public void testDontStartWithoutEncoder() {
        appender.setEncoder(null);
        appender.start();
        assertFalse("isStarted", appender.isStarted());
        assertThat(ctx.getStatusManager().getCopyOfStatusList(),
                hasItem(new ErrorStatus("No encoder set for the appender named [\"kafkaAppenderBase\"].", null)));
    }

    @Test
    public void testAppendUsesKeying() {
        when(encoder.encode(ArgumentMatchers.any(ILoggingEvent.class))).thenReturn(new byte[]{0x00, 0x00});
        appender.start();
        final LoggingEvent evt = new LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "message", null, new Object[0]);
        appender.append(evt);
        verify(deliveryStrategy).send(ArgumentMatchers.any(KafkaProducer.class), ArgumentMatchers.any(ProducerRecord.class), ArgumentMatchers.eq(evt), ArgumentMatchers.any(FailedDeliveryCallback.class));
        verify(keyingStrategy).createKey(ArgumentMatchers.same(evt));
        verify(deliveryStrategy).send(ArgumentMatchers.any(KafkaProducer.class), ArgumentMatchers.any(ProducerRecord.class), ArgumentMatchers.eq(evt), ArgumentMatchers.any(FailedDeliveryCallback.class));
    }

    @Test
    public void testAppendUsesPreConfiguredPartition() {
        when(encoder.encode(ArgumentMatchers.any(ILoggingEvent.class))).thenReturn(new byte[]{0x00, 0x00});
        ArgumentCaptor<ProducerRecord> producerRecordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        appender.setPartition(1);
        appender.start();
        final LoggingEvent evt = new LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "message", null, new Object[0]);
        appender.append(evt);
        verify(deliveryStrategy).send(ArgumentMatchers.any(KafkaProducer.class), producerRecordCaptor.capture(), ArgumentMatchers.eq(evt), ArgumentMatchers.any(FailedDeliveryCallback.class));
        final ProducerRecord value = producerRecordCaptor.getValue();
        assertThat(value.partition(), equalTo(1));
    }

    @Test
    public void testKafkaLoggerPrefix() throws ReflectiveOperationException {
        Field constField = KafkaAppender.class.getDeclaredField("KAFKA_LOGGER_PREFIX");
        if (!constField.isAccessible()) {
            constField.setAccessible(true);
        }
        String constValue = (String) constField.get(null);
        assertThat(constValue, equalTo("org.apache.kafka.clients"));
    }

}
