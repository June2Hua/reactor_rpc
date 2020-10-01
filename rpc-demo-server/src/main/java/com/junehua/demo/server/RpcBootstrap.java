package com.junehua.demo.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author JuneHua
 * @Date 2020/9/30 23:41
 * @Version 1.0
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring.xml");
    }
}
