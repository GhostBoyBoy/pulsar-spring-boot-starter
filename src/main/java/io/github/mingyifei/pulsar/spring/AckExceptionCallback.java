package io.github.mingyifei.pulsar.spring;

import io.github.mingyifei.pulsar.model.Channel;
import io.github.mingyifei.pulsar.model.Message;
import java.util.List;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/12/13 4:23 下午
 **/
public interface AckExceptionCallback {

    default void execute(Channel channel, Message<?> message) {
    }

    default List<String> matchTopics(){
        return null;
    }
}
