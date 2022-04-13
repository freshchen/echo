package com.github.freshchen.echo.rpc.transport.netty.handler;

import com.github.freshchen.echo.rpc.common.constant.RpcNettyConstants;
import com.github.freshchen.echo.rpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          RpcProtocol protocol,
                          ByteBuf out) throws Exception {
        log.info(protocol.toString());
        out.writeShort(RpcNettyConstants.MAGIC);
        byte[] data = protocol.getData();
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
