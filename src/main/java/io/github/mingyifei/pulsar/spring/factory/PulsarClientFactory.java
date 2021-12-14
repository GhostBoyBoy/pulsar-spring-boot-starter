package io.github.mingyifei.pulsar.spring.factory;

import io.github.mingyifei.pulsar.spring.Factory;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @Description pulsar连接工厂
 * @Author ming.yifei
 * @Date 2021/11/1 10:19 上午
 **/
@Slf4j
@Component
@SuppressWarnings("all")
@ConfigurationProperties(prefix = "pulsar.client")
public class PulsarClientFactory implements Factory<PulsarClient> {
    private static final int CPU_CORE = Runtime.getRuntime().availableProcessors();
    private String serviceUrl;
    private Integer ioThreads = CPU_CORE;
    private Integer listenerThreads = CPU_CORE * 10;
    private Boolean enableTcpNoDelay = false;
    private Integer statsInterval = 60;
    private Integer keepAliveInterval = 30;
    private Integer connectionTimeout = 10;
    private Integer operationTimeout = 10;

    @Override
    @SneakyThrows
    public PulsarClient create() {
        Assert.hasText(serviceUrl, "Pulsar服务地址{pulsar.client.serviceUrl}未指定");
        return PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .ioThreads(ioThreads)
                .listenerThreads(listenerThreads)
                .enableTcpNoDelay(enableTcpNoDelay)
                .statsInterval(statsInterval, TimeUnit.SECONDS)
                .keepAliveInterval(keepAliveInterval, TimeUnit.SECONDS)
                .connectionTimeout(connectionTimeout, TimeUnit.SECONDS)
                .operationTimeout(operationTimeout, TimeUnit.SECONDS)
                .build();
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
