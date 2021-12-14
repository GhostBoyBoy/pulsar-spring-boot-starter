package io.github.mingyifei.pulsar.spring.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import io.github.mingyifei.pulsar.spring.Factory;
import io.github.mingyifei.pulsar.config.PulsarConsumerProperties;
import io.github.mingyifei.pulsar.model.Channel;
import io.github.mingyifei.pulsar.model.Message;
import io.github.mingyifei.pulsar.spring.AckExceptionCallback;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionType;

/**
 * @Description consumer工厂
 * @Author ming.yifei
 * @Date 2021/10/29 6:04 下午
 **/
@Slf4j
@Builder
public class PulsarConsumerFactory implements Factory<Consumer<byte[]>> {

    private final PulsarConsumerProperties consumerProperties;

    private final Collection<AckExceptionCallback> ackExceptionCallbacks;

    private final Method method;

    private final Object bean;

    private final Class<?>[] params;

    private final PulsarClient pulsarClient;

    private final String targetTopicUrl;

    private final String sourceTopicUrl;

    @Override
    public Consumer<byte[]> create() {
        try {
            ConsumerBuilder<byte[]> consumerBuilder =
                    pulsarClient.newConsumer(Schema.BYTES)
                            .consumerName(consumerProperties.getConsumerName())
                            .subscriptionName(consumerProperties.getSubscriptionName())
                            .subscriptionType(consumerProperties.getSubscriptionType())
                            .receiverQueueSize(consumerProperties.getReceiverQueueSize())
                            .autoUpdatePartitions(true)
                            .autoUpdatePartitionsInterval(30, TimeUnit.SECONDS)
                            .messageListener((consumer, msg) -> {
                                Message<?> message = null;
                                Channel channel = null;
                                try {
                                    message = new Message<>(
                                            msg.getValue(),
                                            msg.getProperties(),
                                            msg.getMessageId(), msg.getPublishTime(), msg.getSequenceId(),
                                            msg.getKey());
                                    channel = new Channel(consumer, message, msg.getValue());
                                    if (params.length == 1) {
                                        method.invoke(bean, channel);
                                    } else {
                                        deserialize(msg, message);
                                        if (params[0].isAssignableFrom(Channel.class)) {
                                            method.invoke(bean, channel, message);
                                        } else {
                                            method.invoke(bean, message, channel);
                                        }
                                    }
                                } catch (Exception e) {
                                    if (e instanceof InvocationTargetException) {
                                        e = (Exception)((InvocationTargetException) e).getTargetException();
                                    }
                                    log.error("method Invocation fail. method: [{}#{}] ", bean.getClass().getName(),
                                            method.getName(), e);
                                    consumerProperties.getErrorStrategy().execute(channel, message);
                                    if (CollectionUtils.isNotEmpty(ackExceptionCallbacks)) {
                                        Channel finalChannel = channel;
                                        Message<?> finalMessage = message;
                                        ackExceptionCallbacks.forEach(callback -> {
                                            List<String> matchTopics = callback.matchTopics();
                                            if (CollectionUtils.isEmpty(matchTopics)) {
                                                callback.execute(finalChannel, finalMessage);
                                            } else {
                                                boolean anyMatch = matchTopics.stream()
                                                        .anyMatch(sourceTopicUrl::equals);
                                                if (anyMatch) {
                                                    callback.execute(finalChannel, finalMessage);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
            // 分块
            if (consumerProperties.isChunk()) {
                consumerBuilder.expireTimeOfIncompleteChunkedMessage(3, TimeUnit.MINUTES);
                consumerBuilder.subscriptionType(SubscriptionType.Exclusive);
                consumerBuilder.subscriptionMode(SubscriptionMode.Durable);
                consumerBuilder.autoAckOldestChunkedMessageOnQueueFull(false);
                consumerBuilder.maxPendingChunkedMessage(100);
                consumerBuilder.receiverQueueSize(100);
            }

            // 正则匹配
            if (consumerProperties.isRegex()) {
                consumerBuilder.topicsPattern(Pattern.compile(targetTopicUrl));
                consumerBuilder.patternAutoDiscoveryPeriod(10, TimeUnit.SECONDS);
            } else {
                consumerBuilder.topic(targetTopicUrl);
            }

            // 超时
            if (consumerProperties.getAckTimeout() != null
                    && consumerProperties.getAckTimeout() > 0
                    && consumerProperties.getTimeUnit() != null) {
                consumerBuilder.ackTimeout(consumerProperties.getAckTimeout(), consumerProperties.getTimeUnit());
            }
            // 死信策略
            if (consumerProperties.getMaxRedeliverCount() != null && consumerProperties.getMaxRedeliverCount() > 0) {
                consumerBuilder.deadLetterPolicy(DeadLetterPolicy.builder()
                        .maxRedeliverCount(consumerProperties.getMaxRedeliverCount())
                        .build());
            }
            // 重新投递
            if (consumerProperties.getNegativeAckRedeliveryDelay() != null
                    && consumerProperties.getNegativeAckRedeliveryDelay() > 0
                    && consumerProperties.getTimeUnit() != null) {
                consumerBuilder.negativeAckRedeliveryDelay(consumerProperties.getNegativeAckRedeliveryDelay(),
                        consumerProperties.getTimeUnit());
            }

            return consumerBuilder.subscribe();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 写入value
     * @Param: [msg, message]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/12/13
     **/
    private void deserialize(org.apache.pulsar.client.api.Message<byte[]> msg, Message<?> message) {
        message.setData(JSON.parseObject(msg.getValue(), consumerProperties.getType(),
                Feature.AutoCloseSource,
                Feature.InternFieldNames,
                Feature.UseBigDecimal,
                Feature.AllowUnQuotedFieldNames,
                Feature.AllowSingleQuotes,
                Feature.AllowArbitraryCommas,
                Feature.SortFeidFastMatch,
                Feature.IgnoreNotMatch
        ));
    }

}
