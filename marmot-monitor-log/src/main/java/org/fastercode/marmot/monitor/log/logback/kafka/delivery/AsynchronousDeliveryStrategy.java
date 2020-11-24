package org.fastercode.marmot.monitor.log.logback.kafka.delivery;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.TimeoutException;

public class AsynchronousDeliveryStrategy implements DeliveryStrategy {

    @Override
    public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, final E event,
                                  final FailedDeliveryCallback<E> failedDeliveryCallback) {
        try {
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null && failedDeliveryCallback != null) {
                        failedDeliveryCallback.onFailedDelivery(event, exception);
                    }
                }
            });
            return true;
        } catch (BufferExhaustedException | TimeoutException e) {
            if (failedDeliveryCallback != null) {
                failedDeliveryCallback.onFailedDelivery(event, e);
            }
            return false;
        }
    }

}
