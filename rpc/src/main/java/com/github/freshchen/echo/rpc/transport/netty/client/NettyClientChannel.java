package com.github.freshchen.echo.rpc.transport.netty.client;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Data
public class NettyClientChannel {

    private Channel channel;

    public static NettyClientChannel of(Channel channel) {
        NettyClientChannel nettyClientChannel = new NettyClientChannel();
        nettyClientChannel.setChannel(channel);
        return nettyClientChannel;
    }
}
