package io.github.mingyifei.pulsar.spring.el;

import io.github.mingyifei.pulsar.utils.PulsarUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/12/13 2:35 下午
 **/
@Component
public class ResolverValue implements EmbeddedValueResolverAware {

    private StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * @Description: 解析
     * @Param: [key]
     * @return: java.lang.String
     * @Author: ming.yifei
     * @Date: 2021/12/13
     **/
    public String parse(String key) {
        if (key.startsWith(PulsarUtils.EL_START) && key.endsWith(PulsarUtils.EL_END)) {
            return resolver.resolveStringValue(key);
        }
        return key;
    }
}
