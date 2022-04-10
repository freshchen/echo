package com.github.freshchen.echo.rpc.transport;

import com.github.freshchen.echo.rpc.transport.netty.client.NettyClientChannel;

/**
 * @author darcy
 * @since 2022/04/09
 **/
public interface Client {

    NettyClientChannel connect(String host, int port);
}
