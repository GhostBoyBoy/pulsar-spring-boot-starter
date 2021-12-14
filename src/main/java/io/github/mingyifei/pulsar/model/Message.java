package io.github.mingyifei.pulsar.model;

import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.apache.pulsar.client.api.MessageId;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/25 5:27 下午
 **/
@Getter
@ToString
public class Message<T> {

    private T data;

    private final Map<String, String> properties;

    private final MessageId messageId;

    private final long publishTime;

    private final long sequenceId;

    private final String key;

    public Message(T data, Map<String, String> properties, MessageId messageId, long publishTime, long sequenceId,
                   String key) {
        this.data = data;
        this.properties = properties;
        this.messageId = messageId;
        this.publishTime = publishTime;
        this.sequenceId = sequenceId;
        this.key = key;
    }

    public void setData(T data) {
        this.data = data;
    }
}
