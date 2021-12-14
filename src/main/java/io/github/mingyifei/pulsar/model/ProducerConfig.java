package io.github.mingyifei.pulsar.model;

import io.github.mingyifei.pulsar.config.PulsarProducerProperties;
import lombok.Builder;
import lombok.Data;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 7:07 下午
 **/
@Data
@Builder
public class ProducerConfig {

    private PulsarProducerProperties producerProperties;

    private String methodName;
}
