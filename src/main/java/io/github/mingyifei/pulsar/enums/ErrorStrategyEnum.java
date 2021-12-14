package io.github.mingyifei.pulsar.enums;

import io.github.mingyifei.pulsar.model.Channel;
import io.github.mingyifei.pulsar.model.Message;
import io.github.mingyifei.pulsar.spring.AckExceptionCallback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/12/13 4:20 下午
 **/
@Slf4j
@Getter
public enum ErrorStrategyEnum implements AckExceptionCallback {

    /**
     * 无
     */
    None() {
        @Override
        public void execute(Channel channel, Message<?> message) {
            // no do
        }
    },
    /**
     * 拒绝ack
     */
    Negative() {
        @Override
        public void execute(Channel channel, Message<?> message) {
            try {
                channel.negativeAcknowledge(message);
            } catch (Exception e) {
                log.error("回调异常, msg:{}", message, e);
                throw e;
            }
        }
    },
    /**
     * ack
     */
    ACK(){
        @Override
        public void execute(Channel channel, Message<?> message) {
            try {
                channel.acknowledge(message);
            } catch (Exception e) {
                log.error("回调异常, msg:{}", message, e);
                throw e;
            }
        }
    };

    private AckExceptionCallback callback;

    ErrorStrategyEnum() {
    }
}
