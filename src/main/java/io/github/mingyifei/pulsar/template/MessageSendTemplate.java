package io.github.mingyifei.pulsar.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.mingyifei.pulsar.spring.register.PulsarInstanceManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Component;

/**
 * @Description 生产者调用模版
 * @Author ming.yifei
 * @Date 2021/10/25 8:43 下午
 **/
@Slf4j
@Component
public class MessageSendTemplate {

    @Resource
    private PulsarInstanceManager instanceManager;

    /**
     * @Description: 异步发送
     * @Param: [topic, message]
     * @return: java.util.concurrent.CompletableFuture<org.apache.pulsar.client.api.MessageId>
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> CompletableFuture<MessageId> sendAsync(String topic, T message) throws PulsarClientException {
        return this.sendAsync(topic, message, new HashMap<>());
    }

    /**
     * @Description: 异步发送 附加内容
     * @Param: [topic, message, properties]
     * @return: java.util.concurrent.CompletableFuture<org.apache.pulsar.client.api.MessageId>
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> CompletableFuture<MessageId> sendAsync(String topic, T message, Map<String, String> properties)
            throws PulsarClientException {
        return this.sendAsync(topic, message, properties, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * @Description: 异步发送
     * @Param: [topic, message, properties]
     * @return: java.util.concurrent.CompletableFuture<org.apache.pulsar.client.api.MessageId>
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> CompletableFuture<MessageId> sendAsync(String topic, T message, long delay, TimeUnit unit)
            throws PulsarClientException {
        return this.sendAsync(topic, message, new HashMap<>(), delay, unit);
    }

    /**
     * @Description: 同步发送
     * @Param: [topic, message]
     * @return: org.apache.pulsar.client.api.MessageId
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> MessageId send(String topic, T message) {
        return this.send(topic, message, new HashMap<>());
    }

    /**
     * @Description: 同步发送 附加内容
     * @Param: [topic, message, properties]
     * @return: org.apache.pulsar.client.api.MessageId
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> MessageId send(String topic, T message, Map<String, String> properties) {
        return this.send(topic, message, properties, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * @Description: 同步发送
     * @Param: [topic, message, properties]
     * @return: org.apache.pulsar.client.api.MessageId
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> MessageId send(String topic, T message, long delay, TimeUnit unit) {
        return this.send(topic, message, new HashMap<>(), delay, unit);
    }

    /**
     * @Description: 同步发送 指定发送延迟时间
     * @Param: [topic, message, properties, delay, unit]
     * @return: org.apache.pulsar.client.api.MessageId
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> MessageId send(String topic, T message, Map<String, String> properties, long delay, TimeUnit unit) {
        try {
            return instanceManager.getProducerByTopic(topic)
                    .newMessage()
                    .value(JSON.toJSONBytes(message,
                            SerializerFeature.SkipTransientField,
                            SerializerFeature.WriteEnumUsingToString,
                            SerializerFeature.WriteMapNullValue,
                            SerializerFeature.SortField))
                    .deliverAfter(delay, unit)
                    .properties(properties)
                    .send();
        } catch (PulsarClientException e) {
            log.error("pulsar send fail msg:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 异步发送 指定发送延迟时间
     * @Param: [topic, message, properties, delay, unit]
     * @return: java.util.concurrent.CompletableFuture<org.apache.pulsar.client.api.MessageId>
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public <T> CompletableFuture<MessageId> sendAsync(String topic, T message, Map<String, String> properties,
                                                      long delay, TimeUnit unit) {
        return instanceManager.getProducerByTopic(topic)
                .newMessage()
                .value(JSON.toJSONBytes(message,
                        SerializerFeature.SkipTransientField,
                        SerializerFeature.WriteEnumUsingToString,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.SortField))
                .deliverAfter(delay, unit)
                .properties(properties)
                .sendAsync();
    }
}
