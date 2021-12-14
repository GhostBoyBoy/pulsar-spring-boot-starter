package io.github.mingyifei.pulsar.spring.factory;

import io.github.mingyifei.pulsar.config.PulsarProducerProperties;
import io.github.mingyifei.pulsar.spring.Factory;
import io.github.mingyifei.pulsar.spring.intercept.ProducerInterceptorCallback;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerAccessMode;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;

/**
 * @Description producer工厂
 * @Author ming.yifei
 * @Date 2021/10/29 5:56 下午
 **/
@Builder
public class PulsarProducerFactory implements Factory<Producer<byte[]>> {

    private final PulsarProducerProperties producerProperties;

    private final Collection<ProducerInterceptorCallback> callbacks;

    private final PulsarClient pulsarClient;

    @Override
    public Producer<byte[]> create() {
        try {
            ProducerBuilder<byte[]> producerBuilder = pulsarClient
                    .newProducer(Schema.BYTES)
                    .topic(producerProperties.getTopicUrl())
                    .maxPendingMessages(producerProperties.getMaxPendingMessages())
                    .sendTimeout(producerProperties.getSendTimeout(), producerProperties.getTimeUnit())
                    .blockIfQueueFull(producerProperties.getBlockIfQueueFull())
                    .autoUpdatePartitions(true)
                    .intercept(new ProducerInterceptor[]{new ProducerInterceptor() {
                        @Override
                        public void close() {
                        }

                        @Override
                        public boolean eligible(Message message) {
                            return true;
                        }

                        @Override
                        public Message beforeSend(Producer producer, Message message) {
                            if (CollectionUtils.isNotEmpty(callbacks)) {
                                io.github.mingyifei.pulsar.model.Message<?> newMessage =
                                        new io.github.mingyifei.pulsar.model.Message<>(message.getValue(),
                                                message.getProperties(), message.getMessageId(),
                                                System.currentTimeMillis(),
                                                message.getSequenceId(), message.getKey());
                                callbacks.forEach(callback -> callback.beforSend(newMessage));
                            }
                            return message;
                        }

                        @Override
                        public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId,
                                                          Throwable exception) {
                        }
                    }})
                    .autoUpdatePartitionsInterval(30, TimeUnit.SECONDS)
                    .accessMode(ProducerAccessMode.Shared);
            // 拆分
            if (producerProperties.getEnableChunking()) {
                producerProperties.setEnableBatching(false);
                producerBuilder.enableBatching(false);
                producerBuilder.enableChunking(true);
            }
            // 批量发送
            if (producerProperties.getEnableBatching()) {
                producerBuilder.enableBatching(true);
                producerBuilder.batchingMaxBytes(producerProperties.getBatchingMaxBytes());
                producerBuilder.batchingMaxMessages(producerProperties.getBatchingMaxMessages());
                producerBuilder.batchingMaxPublishDelay(producerProperties.getBatchingMaxPublishDelay(),
                        producerProperties.getTimeUnit());
            }
            return producerBuilder.create();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }
}
