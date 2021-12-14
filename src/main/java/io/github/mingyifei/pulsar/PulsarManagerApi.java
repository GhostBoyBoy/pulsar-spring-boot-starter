package io.github.mingyifei.pulsar;

import io.github.mingyifei.pulsar.spring.register.PulsarInstanceManager;
import java.util.Collection;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description http api
 * @Author ming.yifei
 * @Date 2021/10/29 4:50 下午
 **/
@Slf4j
@RestController()
@RequestMapping("/pulsar/manager")
public class PulsarManagerApi {

    @Resource
    private PulsarInstanceManager instanceManager;

    @PostMapping(value = "/pause")
    public void pause() {
        try {
            instanceManager.getConsumers()
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(Consumer::pause);
        } catch (Exception e) {
            log.error("consumer pause fail msg:{}", e.getMessage(), e);
        }
    }

    @PostMapping(value = "/resume")
    public void resume() {
        try {
            instanceManager.getConsumers()
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(Consumer::resume);
        } catch (Exception e) {
            log.error("consumer resume fail msg:{}", e.getMessage(), e);
        }
    }
}
