package com.junehua.rpc.client;

import com.junehua.rpc.common.bean.RpcRequest;
import com.junehua.rpc.common.bean.RpcResponse;
import com.junehua.rpc.registry.ServiceDiscovery;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Author JuneHua
 * @Date 2020/9/26 21:38
 * @Version 1.0
 *
 * 客户端代理
 */
@Data
public class RpcProxy {

    private static final Logger log = LoggerFactory.getLogger(RpcProxy.class);

    private String serverAddress;
    private ServiceDiscovery discovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    public <T> T create(final Class<?> interfaceClass, final String serverVersion) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setArgsType(method.getParameterTypes());
                        request.setArgs(args);
                        request.setServiceVersion(serverVersion);
                        //rpc服务
                        String serverName = null;
                        if (discovery != null) {
                            serverName = interfaceClass.getName();
                            if (serverVersion != null && serverVersion.length() != 0) {
                                serverName += "-" + serverVersion;
                            }
                            serverAddress = discovery.discovery(serverName);
                            log.info("InvocationHandler.invoke serverName:{} , address:{}", serverName, serverAddress);
                        }
                        if (serverAddress == null || serverAddress.length() == 0) {
                            log.warn("RpcProxy.create  zookeeper中找不到该类服务 serverName:{}",serverName);
                            throw new RuntimeException("InvocationHandler.invoke 地址为空");
                        }
                        String[] hostAndPort = serverAddress.split(":");
                        String host = hostAndPort[0];
                        String port = hostAndPort[1];
                        RpcClient client = new RpcClient(host, Integer.valueOf(port));
                        log.debug("host:{} port:{}", host, port);
                        //统计RPC请求时间
                        long startTime = System.currentTimeMillis();
                        log.debug("RPC start ....");
                        RpcResponse response = client.send(request);
                        log.debug("RPC end ....");
                        long endTime = System.currentTimeMillis();
                        log.info("RpcProxy.create  use time:{}", endTime - startTime);
                        if (response == null || response.getException() != null) {
                            log.warn("RpcProxy.create  response is null");
                            throw new RuntimeException();
                        }
                        return response.getResult();
                    }
                });
    }
}
