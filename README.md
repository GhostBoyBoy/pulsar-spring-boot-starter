# Spring boot starter for [Apache Pulsar](https://pulsar.apache.org/)

## 快速开始

**简介**

- 普遍性：同一个Topic支持多种任意数据类型传输 例如：
  第一个发送A对象，消费为A。
  第二次发送B对象，接收也可以是A，只要字段对应上即可。
  也可以Map接收，数据类型无限制，发什么都可以。

- 所有配置全面支持SpringEL表达式配置

- 通过`PulsarProducer`注解`enableChunking`开启配置支持大型消息传输

- 支持超复杂数据类型的自定义接收

- 支持消费失败抛异常后策略选择，也支持自定义


#### 1. 添加Maven依赖

```xml
<dependency>
  <groupId>io.github.mingyifei</groupId>
  <artifactId>pulsar-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

#### 2. 配置 生产者

```java
@Slf4j
@Configuration
public class Producer extends Base {

    /**
     * 定义生产者
     */
    @PulsarProducer(topic = "${pulsar.simple.topic}")
    public void producer() {}
}
```

#### 3. 配置消费者

简单消费
```java
@Slf4j
@Component
public class SimpleReceive {
    /**
     * 消费
     */
    @PulsarConsumer(topicUrls = "${pulsar.simple.topic}", type = String.class)
    public void receive(Channel channel) {
        channel.
        log.info("接到消息：{}", message);
        channel.acknowledge(message);
        Message<Map<Map<List<String[]>, byte[]>, String>> mapMessage =
                channel.getMessage(new ReferenceType<Map<Map<List<String[]>, byte[]>, String>>() {
                });
    }
}
```

超复杂数据结构
```java
@Slf4j
@Component
public class SuperDifficult {
  /**
   * 消费
   */
  @PulsarConsumer(topicUrls = "${pulsar.simple.topic}")
  public void receive(Channel channel) {
    Message<Map<Map<List<String[]>, byte[]>, String>> mapMessage =
            channel.getMessage(new ReferenceType<Map<Map<List<String[]>, byte[]>, String>>() {});
    
    Map<Map<List<String[]>, byte[]>, String> data = mapMessage.getData();

    log.info("接到消息：{}", message);
    channel.acknowledge(message);
  }
}
```

#### 4. 发送消息
```java
@Slf4j
@Component
class MyProducer {
    
    @Autowired
    protected MessageSendTemplate template;

    public void send() {
        MessageId messageId = template.send("${pulsar.simple.topic}", "simple message!");
        log.info("发送成功 消息ID:{}", messageId);
    }
}
```


#### 5. 最基本的配置

```properties
spring.application.name=pulsar-demo
spring.profiles.active=test
pulsar.client.serviceUrl=pulsar://localhost:6650
```
pulsar-admin命令
创建完成namespace，执行

```shell
$ pulsar-admin namespaces set-schema-compatibility-strategy -c ALWAYS_COMPATIBLE 租户/命名空间
```
