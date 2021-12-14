package io.github.mingyifei.pulsar.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/12/13 11:36 上午
 **/
public class ReferenceType<T> {
    private final Type type;

    protected ReferenceType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (!(superClass instanceof ParameterizedType)) {
            throw new RuntimeException("未指定泛型");
        }
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this.type;
    }
}
