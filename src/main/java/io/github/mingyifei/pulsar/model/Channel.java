package io.github.mingyifei.pulsar.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;

/**
 * @Description
 * 业务方用不到那么多api，所以包装一层，提供基本使用
 * 当前类：Consumer是protected，如果真的有场景使用其他api，可以继承当前类，接收消息时用自定义的
 *
 * @Author ming.yifei
 * @Date 2021/10/25 5:27 下午
 **/
@Slf4j
public class Channel implements SimpleConsumer {

    protected final Consumer<byte[]> consumer;

    protected final Message<?> message;

    private final byte[] bytes;

    public Channel(Consumer<byte[]> consumer, Message<?> message, byte[] bytes) {
        this.consumer = consumer;
        this.message = message;
        this.bytes = bytes;
    }

    @Override
    public <T> Message<T> getMessage(ReferenceType<T> reference) {
        return new Message<>(
                JSON.parseObject(bytes, reference.getType(),
                        Feature.AutoCloseSource,
                        Feature.InternFieldNames,
                        Feature.UseBigDecimal,
                        Feature.AllowUnQuotedFieldNames,
                        Feature.AllowSingleQuotes,
                        Feature.AllowArbitraryCommas,
                        Feature.SortFeidFastMatch,
                        Feature.IgnoreNotMatch
                ),
                message.getProperties(),
                message.getMessageId(),
                message.getPublishTime(),
                message.getSequenceId(),
                message.getKey());
    }

    @Override
    @SneakyThrows(PulsarClientException.class)
    public void acknowledge(MessageId messageId) {
        consumer.acknowledge(messageId);
    }

    @Override
    @SneakyThrows(PulsarClientException.class)
    public void acknowledge(Message<?> message) {
        consumer.acknowledge(message.getMessageId());
    }

    @Override
    public void negativeAcknowledge(MessageId messageId) {
        consumer.negativeAcknowledge(messageId);
    }

    @Override
    public void negativeAcknowledge(Message<?> message) {
        consumer.negativeAcknowledge(message.getMessageId());
    }
}



