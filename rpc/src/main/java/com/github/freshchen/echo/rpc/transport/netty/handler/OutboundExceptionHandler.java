package com.github.freshchen.echo.rpc.transport.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
@ChannelHandler.Sharable
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    public static final OutboundExceptionHandler INSTANCE = new OutboundExceptionHandler();


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener(future -> {
            if (!future.isSuccess()) {
                log.error("netty outbound write error：" + future.cause());
            }
        }));
    }

}
