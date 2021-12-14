package io.github.mingyifei.pulsar.model;

import io.github.mingyifei.pulsar.config.PulsarConsumerProperties;
import java.lang.reflect.Method;
import lombok.Builder;
import lombok.Data;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 3:38 下午
 **/
@Data
@Builder
public class ConsumerConfig {

    private PulsarConsumerProperties consumerProperties;

    private Method method;

    private Object bean;

    private Class<?>[] params;
}
