package com.junehua.rpc.client;

import com.junehua.rpc.common.bean.RpcRequest;
import com.junehua.rpc.common.bean.RpcResponse;
import com.junehua.rpc.common.codec.RpcDecoder;
import com.junehua.rpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author JuneHua
 * @Date 2020/9/26 21:11
 * @Version 1.0
 */
@Data
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger log = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;

    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        log.debug("RpcClient construct ...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.debug("RpcClient.channelRead0 channelHandlerContext:{} rpcResponse:{}", channelHandlerContext, rpcResponse);
        this.response = rpcResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("RpcClient.exceptionCaught ctx:{} cause:{}", ctx, cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new RpcEncoder(RpcRequest.class));
                            pipeline.addLast(new RpcDecoder(RpcResponse.class));
                            pipeline.addLast(this);
                        }
                    });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //连接服务器
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            log.debug("RpcClient.send request:{}", request);
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return response;
        }  catch (Exception exception) {
            log.warn("RpcClient.send error:{}");
            throw new RuntimeException("RpcClient.send error:" + exception);
        } finally {
            group.shutdownGracefully();
        }
    }
}
