package io.github.mingyifei.pulsar.spring.intercept;

import io.github.mingyifei.pulsar.model.Message;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/12/10 8:23 下午
 **/
public interface ProducerInterceptorCallback {

    /**
     * @Description: 发送前执行
     * @Param: [newMessage]
     * @return: void
     * @Author: ming.yifei
     * @Date: 2021/12/10
     **/
    void beforSend(Message<?> newMessage);
}
