package org.fastercode.marmot.monitor.log.logback.kafka;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.AppenderAttachable;
import lombok.Getter;
import lombok.Setter;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.AsynchronousDeliveryStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.delivery.DeliveryStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.keying.KeyingStrategy;
import org.fastercode.marmot.monitor.log.logback.kafka.keying.NoKeyKeyingStrategy;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

public abstract class KafkaAppenderConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

    @Setter
    @Getter
    protected String topic = null;

    @Setter
    @Getter
    protected Encoder<E> encoder = null;

    @Setter
    @Getter
    protected KeyingStrategy<? super E> keyingStrategy = null;

    @Setter
    @Getter
    protected DeliveryStrategy deliveryStrategy = null;

    @Setter
    @Getter
    protected Integer partition = null;

    @Setter
    @Getter
    protected boolean appendTimestamp = true;

    @Getter
    protected Map<String, Object> producerConfig = new HashMap<String, Object>();

    public void addProducerConfig(String keyValue) {
        String[] split = keyValue.split("=", 2);
        if (split.length == 2)
            addProducerConfigValue(split[0], split[1]);
    }

    public void addProducerConfigValue(String key, Object value) {
        this.producerConfig.put(key, value);
    }

    protected boolean checkPrerequisites() {
        boolean errorFree = true;

        if (producerConfig.get(BOOTSTRAP_SERVERS_CONFIG) == null) {
            addError("No \"" + BOOTSTRAP_SERVERS_CONFIG + "\" set for the appender named [\""
                    + name + "\"].");
            errorFree = false;
        }

        if (topic == null) {
            addError("No topic set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }

        if (encoder == null) {
            addError("No encoder set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }

        if (keyingStrategy == null) {
            addInfo("No explicit keyingStrategy set for the appender named [\"" + name + "\"]. Using default NoKeyKeyingStrategy.");
            keyingStrategy = new NoKeyKeyingStrategy();
        }

        if (deliveryStrategy == null) {
            addInfo("No explicit deliveryStrategy set for the appender named [\"" + name + "\"]. Using default asynchronous strategy.");
            deliveryStrategy = new AsynchronousDeliveryStrategy();
        }

        return errorFree;
    }

}
