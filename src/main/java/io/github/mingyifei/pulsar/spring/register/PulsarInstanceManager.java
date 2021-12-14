package io.github.mingyifei.pulsar.spring.register;

import io.github.mingyifei.pulsar.spring.el.ResolverValue;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @Description pulsar 客户端实例维护 供外部调用使用
 * @Author ming.yifei
 * @Date 2021/10/29 3:35 下午
 **/
@Component
public class PulsarInstanceManager {

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private ResolverValue resolverValue;

    private Map<String, Producer<byte[]>> producers;

    private Map<String, List<Consumer<byte[]>>> consumers;

    public Producer<byte[]> getProducerByTopic(String topic) {
        Producer<byte[]> producer = producers.get(getTopicUrl(topic));
        Assert.notNull(producer, topic + "不存在");
        return producer;
    }

    public List<Consumer<byte[]>> getConsumerByTopic(String topic) {
        List<Consumer<byte[]>> consumerList = consumers.get(getTopicUrl(topic));
        Assert.notEmpty(consumerList, topic + "不存在");
        return consumerList;
    }

    public Map<String, Producer<byte[]>> getProducers() {
        return this.producers;
    }

    public Map<String, List<Consumer<byte[]>>> getConsumers() {
        return this.consumers;
    }

    void setProducers(Map<String, Producer<byte[]>> producers) {
        this.producers = producers;
    }

    void setConsumers(Map<String, List<Consumer<byte[]>>> consumers) {
        this.consumers = consumers;
    }

    /**
     * @Description: 环境url拼接
     * @Param: [topicUrl]
     * @return: java.lang.String
     * @Author: ming.yifei
     * @Date: 2021/12/9
     **/
    public String getTopicUrl(String topicUrl) {
        return env.toLowerCase() + "-" + resolverValue.parse(topicUrl);
    }
}
