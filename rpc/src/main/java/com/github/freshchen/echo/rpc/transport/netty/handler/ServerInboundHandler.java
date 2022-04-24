package com.github.freshchen.echo.rpc.transport.netty.handler;

import com.github.freshchen.echo.rpc.protocol.RpcProto;
import com.github.freshchen.echo.rpc.server.RpcServiceHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@ChannelHandler.Sharable
public class ServerInboundHandler extends SimpleChannelInboundHandler<RpcProto.Package> {

    private RpcServiceHandler rpcServiceHandler;

    public ServerInboundHandler(RpcServiceHandler rpcServiceHandler) {
        this.rpcServiceHandler = rpcServiceHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProto.Package msg) throws Exception {
        rpcServiceHandler.handleRequest(ctx, msg);
    }
}
