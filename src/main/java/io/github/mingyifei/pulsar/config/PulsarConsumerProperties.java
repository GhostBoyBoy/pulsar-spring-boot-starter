package io.github.mingyifei.pulsar.config;

import io.github.mingyifei.pulsar.enums.ErrorStrategyEnum;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pulsar.client.api.SubscriptionType;

/**
 * @Description consumer配置
 * @Author ming.yifei
 * @Date 2021/10/25 3:18 下午
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PulsarConsumerProperties {

    private List<String> topicUrls;

    private Class<?> type;

    private boolean isRegex;

    private boolean isChunk;

    private SubscriptionType subscriptionType;

    private Integer consumerSize;

    private String consumerName;

    private String subscriptionName;

    private Integer maxRedeliverCount;

    private Integer ackTimeout;

    private Integer negativeAckRedeliveryDelay;

    private TimeUnit timeUnit;

    private Integer receiverQueueSize;

    private ErrorStrategyEnum errorStrategy;
}
