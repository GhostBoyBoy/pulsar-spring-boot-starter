package io.github.mingyifei.pulsar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 2:44 下午
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PulsarConsumer {

    String[] topicUrls();

    /**
     * 接收类型
     */
    Class<?> type() default byte[].class;

    /**
     * topic url是否使用正则匹配
     */
    String isRegex() default "false";

    /**
     * 支持大型消息传输
     */
    String isChunk() default "false";

    /**
     * 消费类型 Exclusive, Shared, Failover, Key_Shared
     */
    String subscriptionType() default "Shared";

    /**
     * 消费者数量
     */
    String consumerSize() default "1";

    /**
     * 分组名称
     */
    String subscriptionName() default "${spring.application.name}";

    /**
     * 重试次数
     */
    String maxRedeliverCount() default "-1";

    /**
     * ack超时
     */
    String ackTimeout() default "-1";

    /**
     * ack拒绝后等待多久进行重新投递
     */
    String negativeAckRedeliveryDelay() default "1000";

    /**
     * 单位
     */
    String timeUnit() default "MILLISECONDS";

    /**
     * 接收队列大小
     */
    String receiverQueueSize() default "1000";

    /**
     * 消费异常策略 None, Negative, ACK
     * 对所有topic生效，若指定topic策略，实现@See AckExceptionCallback接口
     */
    String errorStrategy() default "None";
}
