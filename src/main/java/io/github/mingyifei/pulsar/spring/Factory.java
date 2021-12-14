package io.github.mingyifei.pulsar.spring;

/**
 * @Description TODO
 * @Author ming.yifei
 * @Date 2021/10/29 5:54 下午
 **/
public interface Factory<T> {

    /**
     * @Description: 创建
     * @Param: []
     * @return: T
     * @Author: ming.yifei
     * @Date: 2021/11/1
     **/
    T create();
}
