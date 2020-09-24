package com.junehua.rpc.common.codec;

import com.junehua.rpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author JuneHua
 * @Date 2020/9/24 23:32
 * @Version 1.0
 */
public class RpcEncoder extends MessageToByteEncoder {

    //待序列化的类
    private Class<?> encodeObject;

    public RpcEncoder(Class<?> encodeObject) {
        this.encodeObject = encodeObject;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (encodeObject.isInstance(o)) {
            byte[] bytes = SerializationUtil.serialize(o);
            //显示序列化大小，用于TCP粘包
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
