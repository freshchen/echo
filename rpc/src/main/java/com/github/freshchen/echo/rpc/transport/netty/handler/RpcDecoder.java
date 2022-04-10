package com.github.freshchen.echo.rpc.transport.netty.handler;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.transport.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.github.freshchen.echo.rpc.common.constant.RpcNettyConstants.*;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object result = decode0(in);
        if (result != null) {
            out.add(result);
        }
    }

    private Object decode0(ByteBuf in) {
        if (in.readableBytes() < HEADER_LENGTH) {
            return null;
        }
        in.markReaderIndex();
        int readerIndex = in.readerIndex();
        short magic = in.readShort();
        if (MAGIC != magic) {
            in.skipBytes(in.readableBytes());
            throw new RpcException("magic header error: " );
        }
        in.readerIndex(readerIndex + MAGIC_LENGTH);
        int size = in.readInt();
        byte[] data = new byte[size];
        in.readBytes(data);
        RpcProtocol rpcPacket = RpcProtocol.builder().data(data).build();
        log.info(rpcPacket.toString());
        return rpcPacket;
    }
}
