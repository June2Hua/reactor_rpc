package com.junehua.demo.server;

import com.junehua.demo.service.Helloservice;
import com.junehua.rpc.server.RpcServiceAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author JuneHua
 * @Date 2020/9/30 23:35
 * @Version 1.0
 */
@RpcServiceAnnotation(value = Helloservice.class)
public class HelloServiceImpl implements Helloservice {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(String name) {
        log.info("HelloServiceImpl.hello name:{}", name);
        return "Hello " + name;
    }
}
