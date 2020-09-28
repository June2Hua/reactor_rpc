package com.junehua.rpc.server;

import com.junehua.rpc.common.bean.RpcRequest;
import com.junehua.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author JuneHua
 * @Date 2020/9/26 22:40
 * @Version 1.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger log = LoggerFactory.getLogger(RpcServerHandler.class);

    private RpcRequest request;

    private final Map<String, Object> serviceName2ServiceBean;

    public RpcServerHandler(Map<String, Object> serviceName2ServiceBean) {
        this.serviceName2ServiceBean = serviceName2ServiceBean;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        this.request = request;
        RpcResponse response = new RpcResponse();
        try {
            Object result = handle(request);
            response.setRequestId(request.getRequestId());
            response.setResult(result);
        } catch (Exception exception) {
            log.warn("RpcServerHandler.channelRead0 requestId:{}", request.getRequestId());
            response.setException(exception);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("RpcServerHandler.exceptionCaught ctx:{} cause:{}", ctx, cause);
        ctx.close();
    }

    private Object handle(RpcRequest request) throws Exception {
        String className = request.getClassName();
        String serviceVersion = request.getServiceVersion();
        if (serviceVersion != null && serviceVersion.length() != 0) {
            className += "-" + serviceVersion;
        }
        Object serviceBean = serviceName2ServiceBean.get(className);
        if (serviceBean == null) {
            log.warn("RpcServerHandler.handle serviceBean is null className:{} serviceVersion:{}", className, serviceVersion);
            throw new RuntimeException("serviceBean is null");
        }
        //请求参数
        Class<?> clazz = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] argsType = request.getArgsType();
        Object[] args = request.getArgs();
        //cglib反射调用服务提供方法
        FastClass serviceFastClass = FastClass.create(clazz);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, argsType);
        return serviceFastMethod.invoke(serviceBean, args);
    }
}
