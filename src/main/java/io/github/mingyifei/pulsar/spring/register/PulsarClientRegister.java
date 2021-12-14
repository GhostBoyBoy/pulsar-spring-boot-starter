package io.github.mingyifei.pulsar.spring.register;

import io.github.mingyifei.pulsar.config.PulsarProducerProperties;
import io.github.mingyifei.pulsar.model.ConsumerConfig;
import io.github.mingyifei.pulsar.spring.factory.PulsarClientFactory;
import io.github.mingyifei.pulsar.spring.factory.PulsarProducerFactory;
import com.google.common.collect.Lists;
import io.github.mingyifei.pulsar.config.PulsarConsumerProperties;
import io.github.mingyifei.pulsar.model.ProducerConfig;
import io.github.mingyifei.pulsar.spring.AckExceptionCallback;
import io.github.mingyifei.pulsar.spring.factory.PulsarConsumerFactory;
import io.github.mingyifei.pulsar.spring.intercept.ProducerInterceptorCallback;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description pulsar 客户端注册 connection、consumer、producer
 * @Author ming.yifei
 * @Date 2021/10/25 1:56 下午
 **/
@Slf4j
@Component
public class PulsarClientRegister implements ApplicationRunner, ApplicationContextAware {

    /**
     * 供外部使用
     */
    private final PulsarInstanceManager instanceManager;

    /**
     * 数据源连接管理
     */
    private PulsarClient pulsarClient;

    /**
     * producer k：topic url v：instance
     */
    private final Map<String, Producer<byte[]>> producers = new HashMap<>(16);

    /**
     * producer k：topic url v：instance
     */
    private final Map<String, List<Consumer<byte[]>>> consumers = new HashMap<>(16);

    /**
     * producer配置
     */
    private final List<ProducerConfig> producerConfigs = new CopyOnWriteArrayList<>();

    /**
     * consumer配置
     */
    private final List<ConsumerConfig> consumerConfigs = new CopyOnWriteArrayList<>();

    private Collection<ProducerInterceptorCallback> callbacks;

    private Collection<AckExceptionCallback> exceptionCallbacks;

    private final AtomicBoolean start = new AtomicBoolean(false);

    public PulsarClientRegister(PulsarInstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!start.get() && start.compareAndSet(false, true)) {
            initProducer();
            initConsumer();
            instanceManager.setProducers(Collections.unmodifiableMap(producers));
            instanceManager.setConsumers(Collections.unmodifiableMap(consumers));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initConnection(applicationContext);
        callbacks = applicationContext.getBeansOfType(ProducerInterceptorCallback.class)
                .values();
        exceptionCallbacks = applicationContext.getBeansOfType(AckExceptionCallback.class)
                .values();
    }

    /**
     * @Description: 注册consumer配置
     * @Param: [config]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public void registerConsumer(ConsumerConfig config) {
        this.consumerConfigs.add(config);
    }

    /**
     * @Description: 注册producer配置
     * @Param: [config]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    public void registerProducer(ProducerConfig config) {
        this.producerConfigs.add(config);
    }

    /**
     * @Description: connection初始化
     * @Param: [applicationContext]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    private void initConnection(ApplicationContext applicationContext) {
        this.pulsarClient = applicationContext.getBean(PulsarClientFactory.class)
                .create();
    }

    /**
     * @Description: consumer初始化
     * @Param: []
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    private void initConsumer() {
        consumerConfigs.forEach(config -> {
            PulsarConsumerProperties consumerProperties = config.getConsumerProperties();

            consumerProperties.getTopicUrls()
                    .forEach(topicUrl -> {
                        String targetTopicUrl = instanceManager.getTopicUrl(topicUrl);
                        log.info("consumer init topic:{}", targetTopicUrl);
                        int consumerSize = consumerProperties.getConsumerSize();
                        PulsarConsumerFactory build = PulsarConsumerFactory.builder()
                                .consumerProperties(consumerProperties)
                                .params(config.getParams())
                                .pulsarClient(pulsarClient)
                                .bean(config.getBean())
                                .ackExceptionCallbacks(exceptionCallbacks)
                                .method(config.getMethod())
                                .targetTopicUrl(targetTopicUrl)
                                .sourceTopicUrl(topicUrl)
                                .build();
                        for (int i = 1; i <= consumerSize; i++) {
                            Consumer<byte[]> consumer = build.create();
                            this.consumers.putIfAbsent(targetTopicUrl, Lists.newCopyOnWriteArrayList());
                            this.consumers.get(targetTopicUrl).add(consumer);
                        }
                    });
        });
    }

    /**
     * @Description: producer初始化
     * @Param: []
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    private void initProducer() {
        producerConfigs.forEach(config -> {
            PulsarProducerProperties producerProperties = config.getProducerProperties();
            producerProperties.setTopicUrl(instanceManager.getTopicUrl(producerProperties.getTopicUrl()));
            log.info("producer init topic:{}", producerProperties.getTopicUrl());
            Producer<byte[]> producer = PulsarProducerFactory.builder()
                    .pulsarClient(pulsarClient)
                    .producerProperties(producerProperties)
                    .callbacks(callbacks)
                    .build()
                    .create();
            this.producers.putIfAbsent(producerProperties.getTopicUrl(), producer);
        });
    }
}
