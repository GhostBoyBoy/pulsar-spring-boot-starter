package io.github.mingyifei.pulsar.config;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pulsar.client.api.ProducerAccessMode;

/**
 * @Description producer配置
 * @Author ming.yifei
 * @Date 2021/10/25 3:19 下午
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PulsarProducerProperties {

    private String topicUrl;

    private Integer sendTimeout;

    private Integer maxPendingMessages;

    private TimeUnit timeUnit;

    private Boolean blockIfQueueFull;

    private Boolean enableBatching;
    private Integer batchingMaxBytes;
    private Integer batchingMaxMessages;
    private Integer batchingMaxPublishDelay;

    private Boolean enableChunking;
}
