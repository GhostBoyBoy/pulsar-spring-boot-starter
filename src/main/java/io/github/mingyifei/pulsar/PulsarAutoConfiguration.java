package io.github.mingyifei.pulsar;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 1:51 下午
 **/
@Configuration
@ComponentScan(basePackages = {"io.github.mingyifei.pulsar"})
public class PulsarAutoConfiguration {

}
