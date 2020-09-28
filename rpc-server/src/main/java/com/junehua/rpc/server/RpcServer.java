package com.junehua.rpc.server;

import com.junehua.rpc.common.bean.RpcRequest;
import com.junehua.rpc.common.bean.RpcResponse;
import com.junehua.rpc.common.codec.RpcDecoder;
import com.junehua.rpc.common.codec.RpcEncoder;
import com.junehua.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author JuneHua
 * @Date 2020/9/26 22:40
 * @Version 1.0
 */
public class RpcServer  implements ApplicationContextAware, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> serverName2ServerObject = new HashMap<>();

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new RpcDecoder(RpcRequest.class));//解码
                            pipeline.addLast(new RpcEncoder(RpcResponse.class));//编码
                            pipeline.addLast(new RpcServerHandler(serverName2ServerObject));//处理器
                            log.info("RpcServer.afterPropertiesSet.initChannel 初始化编解码完成");
                        }
                    });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] hostAndPort = serviceAddress.split(":");
            String host = hostAndPort[0];
            int port = Integer.valueOf(hostAndPort[1]);
            // 启动服务器
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            // 注册ip到服务发现zookeeper
            if (serviceRegistry != null) {
                for (String serverName : serverName2ServerObject.keySet()) {
                    serviceRegistry.register(serverName, serviceAddress);
                }
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //扫描自定义注解类
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcServiceAnnotation.class);
        if (beansWithAnnotation != null && beansWithAnnotation.size() != 0) {
            for (Object objectBean : beansWithAnnotation.values()) {
                RpcServiceAnnotation annotation = objectBean.getClass().getAnnotation(RpcServiceAnnotation.class);
                String serverName = annotation.value().getName();
                String serverVersion = annotation.version();
                if (StringUtils.isNotEmpty(serverVersion)) {
                    serverName += "-" + serverVersion;
                }
                serverName2ServerObject.put(serverName, objectBean);
            }
        }
    }
}
