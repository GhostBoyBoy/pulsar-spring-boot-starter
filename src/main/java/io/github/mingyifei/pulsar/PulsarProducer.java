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
public @interface PulsarProducer {

    /**
     * topic
     */
    String topic();

    /**
     * 发送超时
     */
    String sendTimeout() default "-1";

    /**
     * 最大发送缓存队列大小
     */
    String maxPendingMessages() default "1000";

    /**
     * 发送缓存队列满了是否阻塞
     */
    String blockIfQueueFull() default "false";

    /**
     * 开启批量
     */
    String enableBatching() default "true";

    /**
     * 最大字节大小
     */
    String batchingMaxBytes() default "131072";

    /**
     * 最大消息数量
     */
    String batchingMaxMessages() default "1000";

    /**
     * 最大延迟时间
     */
    String batchingMaxPublishDelay() default "1";

    /**
     * 大型消息传输
     */
    String enableChunking() default "false";

    /**
     * 时间单位
     */
    String timeUnit() default "MILLISECONDS";
}
