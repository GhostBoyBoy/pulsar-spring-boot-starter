package io.github.mingyifei.pulsar.model;

import java.util.function.Supplier;
import org.apache.pulsar.client.api.MessageId;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 5:40 下午
 **/
public interface SimpleConsumer {

    default <T> Message<T> getMessage(Supplier<ReferenceType<T>> supplier) {
        return getMessage(supplier.get());
    }

    <T> Message<T> getMessage(ReferenceType<T> reference);

    /**
     * @Description: ack
     * @Param: [messageId]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    void acknowledge(MessageId messageId);

    void acknowledge(Message<?> message);

    /**
     * @Description: 拒绝ack
     * @Param: [messageId]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    void negativeAcknowledge(MessageId messageId);

    void negativeAcknowledge(Message<?> message);

}
