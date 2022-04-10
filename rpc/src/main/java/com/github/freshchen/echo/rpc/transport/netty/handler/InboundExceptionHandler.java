package com.github.freshchen.echo.rpc.transport.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
@ChannelHandler.Sharable
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {

    public static final InboundExceptionHandler INSTANCE = new InboundExceptionHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause != null) {
            log.error("netty inbound error", cause);
        }
    }


}
