package com.junehua.demo.server;

import com.junehua.demo.service.Helloservice;
import com.junehua.rpc.server.RpcServiceAnnotation;

/**
 * @Author JuneHua
 * @Date 2020/9/30 23:35
 * @Version 1.0
 */
@RpcServiceAnnotation(value = Helloservice.class)
public class HelloServiceImpl implements Helloservice {
    @Override
    public String hello(String name) {
        return null;
    }
}
