package com.junehua.rpc.demo.client;

import com.junehua.demo.service.Helloservice;
import com.junehua.rpc.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author JuneHua
 * @Date 2020/9/30 23:34
 * @Version 1.0
 */
public class HelloConsumer {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = applicationContext.getBean("rpcProxy", RpcProxy.class);
        Helloservice helloservice = rpcProxy.create(Helloservice.class);
        String result = helloservice.hello("test");
        System.out.println(result);
    }
}
