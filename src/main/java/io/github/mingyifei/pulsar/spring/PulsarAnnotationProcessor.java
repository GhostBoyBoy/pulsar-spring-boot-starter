package io.github.mingyifei.pulsar.spring;

import io.github.mingyifei.pulsar.PulsarConsumer;
import io.github.mingyifei.pulsar.PulsarProducer;
import io.github.mingyifei.pulsar.config.PulsarConsumerProperties;
import io.github.mingyifei.pulsar.config.PulsarProducerProperties;
import io.github.mingyifei.pulsar.enums.ErrorStrategyEnum;
import io.github.mingyifei.pulsar.model.Channel;
import io.github.mingyifei.pulsar.model.ConsumerConfig;
import io.github.mingyifei.pulsar.model.Message;
import io.github.mingyifei.pulsar.model.ProducerConfig;
import io.github.mingyifei.pulsar.spring.el.ResolverValue;
import io.github.mingyifei.pulsar.spring.register.PulsarClientRegister;
import io.github.mingyifei.pulsar.utils.PulsarUtils;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.shade.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @Description pulsar processor
 * @Author ming.yifei
 * @Date 2021/10/25 2:39 下午
 **/
@Slf4j
@Component
public class PulsarAnnotationProcessor implements BeanPostProcessor {

    @Resource
    private ResolverValue resolver;

    @Resource
    private PulsarClientRegister register;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        ReflectionUtils.doWithMethods(targetClass, method -> {

            // 构建生产者
            PulsarProducer producer = method.getAnnotation(PulsarProducer.class);
            if (producer != null) {
                // 注解配置
                register.registerProducer(ProducerConfig.builder()
                        .producerProperties(getProducerProperties(producer, method.toString()))
                        .methodName(method.getName())
                        .build());
            }

            // 构建消费者
            PulsarConsumer consumer = method.getAnnotation(PulsarConsumer.class);
            if (consumer != null) {

                Class<?>[] types = method.getParameterTypes();
                String className = bean.getClass().getName();
                if (types.length > 2 || types.length < 1) {
                    throw new RuntimeException("class:" + className + "#" + method.getName()
                            + "期望参数：io.github.mingyifei.pulsar.model.Channel & io.github.mingyifei.pulsar.model"
                            + ".Message<>");
                } else {
                    if (types.length == 1) {
                        if (!types[0].isAssignableFrom(Channel.class)) {
                            throw new RuntimeException("class:" + className + "#" + method.getName()
                                    + "期望参数：io.github.mingyifei.pulsar.model.Channel");
                        }
                    } else {
                        boolean checkFirst =
                                types[0].isAssignableFrom(Channel.class) || types[0].isAssignableFrom(Message.class);
                        boolean checkSecond =
                                types[1].isAssignableFrom(Message.class) || types[1].isAssignableFrom(Channel.class);
                        Assert.isTrue(checkFirst && checkSecond, "class:" + className + "#" + method.getName()
                                + "期望参数：io.github.mingyifei.pulsar.model.Channel & io.github.mingyifei.pulsar.model"
                                + ".Message<>");
                    }
                }
                // 注解配置
                register.registerConsumer(ConsumerConfig.builder()
                        .bean(bean)
                        .method(method)
                        .consumerProperties(getConsumerProperties(consumer, method.toString()))
                        .params(types)
                        .build());
            }
        });
        return bean;
    }

    /**
     * @Description: topic el表达式支持
     * @Param: [userProperties]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/12/10
     **/
    private String elExpress(String key) {
        return resolver.parse(key);
    }

    /**
     * @Description: 获取user配置
     * @Param: [producer]
     * @return: io.github.mingyifei.pulsar.config.PulsarProducerProperties
     * @Author: ming.yifei
     * @Date: 2021/10/27
     **/
    private PulsarProducerProperties getProducerProperties(PulsarProducer producer, String method) {
        String topic = elExpress(producer.topic());
        verifyTopicFormat(topic, method);
        return PulsarProducerProperties.builder()
                .topicUrl(topic)
                .maxPendingMessages(Integer.parseInt(elExpress(producer.maxPendingMessages().trim())))
                .sendTimeout(Math.max(Integer.parseInt(elExpress(producer.sendTimeout().trim())), 0))
                .timeUnit(TimeUnit.valueOf(elExpress(producer.timeUnit().trim())))
                .blockIfQueueFull(Boolean.parseBoolean(elExpress(producer.blockIfQueueFull().trim())))
                .enableBatching(Boolean.parseBoolean(elExpress(producer.enableBatching().trim())))
                .batchingMaxBytes(Integer.parseInt(elExpress(producer.batchingMaxBytes().trim())))
                .batchingMaxMessages(Integer.parseInt(elExpress(producer.batchingMaxMessages().trim())))
                .batchingMaxPublishDelay(Integer.parseInt(elExpress(producer.batchingMaxPublishDelay().trim())))
                .enableChunking(Boolean.parseBoolean(elExpress(producer.enableChunking().trim())))
                .build();
    }

    /**
     * @Description: 获取user配置
     * @Param: [consumer]
     * @return: io.github.mingyifei.pulsar.config.PulsarConsumerProperties
     * @Author: ming.yifei
     * @Date: 2021/10/27
     **/
    private PulsarConsumerProperties getConsumerProperties(PulsarConsumer consumer, String method) {
        return PulsarConsumerProperties.builder()
                .type(consumer.type())
                .subscriptionType(SubscriptionType.valueOf(elExpress(consumer.subscriptionType().trim())))
                .ackTimeout(Integer.parseInt(elExpress(consumer.ackTimeout().trim())))
                .consumerName(appName + ":" + PulsarUtils.getLocalIp())
                .consumerSize(Integer.parseInt(elExpress(consumer.consumerSize().trim())))
                .maxRedeliverCount(Integer.parseInt(elExpress(consumer.maxRedeliverCount().trim())))
                .negativeAckRedeliveryDelay(Integer.parseInt(elExpress(consumer.negativeAckRedeliveryDelay().trim())))
                .receiverQueueSize(Integer.parseInt(elExpress(consumer.receiverQueueSize().trim())))
                .subscriptionName(elExpress(consumer.subscriptionName().trim()))
                .timeUnit(TimeUnit.valueOf(elExpress(consumer.timeUnit().trim())))
                .topicUrls(Stream.of(consumer.topicUrls())
                        .map(s -> {
                            String express = elExpress(s.trim());
                            verifyTopicFormat(express, method);
                            return express;
                        })
                        .distinct()
                        .collect(Collectors.toList()))
                .isRegex(Boolean.parseBoolean(elExpress(consumer.isRegex())))
                .isChunk(Boolean.parseBoolean(elExpress(consumer.isChunk())))
                .errorStrategy(ErrorStrategyEnum.valueOf(elExpress(consumer.errorStrategy().trim())))
                .build();
    }

    public void verifyTopicFormat(String topic, String method) {
        Assert.hasText(topic, "topicUrl不能为空 method：" + method);
        String[] split = topic.split("/");
        Assert.isTrue(split.length == 3, "topicUrl格式配置错误 method：" + method);
        Assert.isTrue(Stream.of(split)
                .allMatch(StringUtils::isNotBlank), "topicUrl格式配置错误 method：" + method);
    }
}
