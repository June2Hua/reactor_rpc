package com.junehua.rpc.common.codec;

import com.junehua.rpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author JuneHua
 * @Date 2020/9/24 23:32
 * @Version 1.0
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> decoderObject;

    public RpcDecoder(Class<?> decoderObject) {
        this.decoderObject = decoderObject;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //先读取字节流的4B
        if (byteBuf.readableBytes() < 4) {
            return ;
        }
        byteBuf.markReaderIndex();
        int objectLength = byteBuf.readInt();
        //字节流中对象还未传输完成，则取消读取
        if (byteBuf.readableBytes() < objectLength) {
            byteBuf.resetReaderIndex();
            return ;
        }
        byte[] bytes = new byte[objectLength];
        byteBuf.readBytes(bytes);
        Object object = SerializationUtil.deserialize(bytes, decoderObject);
        list.add(object);
    }
}
