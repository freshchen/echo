package com.github.freshchen.echo.rpc.transport.netty.handler;

import com.github.freshchen.echo.rpc.client.model.RpcClientContext;
import com.github.freshchen.echo.rpc.protocol.RpcProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@ChannelHandler.Sharable
public class ClientInboundHandler extends SimpleChannelInboundHandler<RpcProto.Package> {
    public static final ClientInboundHandler INSTANCE = new ClientInboundHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProto.Package msg) throws Exception {
        RpcClientContext.setResponse(msg);
    }
}
