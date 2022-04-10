package com.github.freshchen.echo.rpc.transport.netty.client;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.config.RpcClientConfiguration;
import com.github.freshchen.echo.rpc.transport.Client;
import com.github.freshchen.echo.rpc.transport.netty.handler.InboundExceptionHandler;
import com.github.freshchen.echo.rpc.transport.netty.handler.OutboundExceptionHandler;
import com.github.freshchen.echo.rpc.transport.netty.handler.RpcDecoder;
import com.github.freshchen.echo.rpc.transport.netty.handler.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.freshchen.echo.rpc.common.constant.RpcNettyConstants.DEFAULT_WORKER_THREAD_NUMBER;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
@NoArgsConstructor
public class NettyClient implements Client {

    private static AtomicBoolean started = new AtomicBoolean(false);

    private RpcClientConfiguration.NettyClientConfiguration.Config config;

    private EventLoopGroup workerGroup;
    private Bootstrap clientBootstrap;

    public NettyClient(RpcClientConfiguration.NettyClientConfiguration.Config config) {
        this.config = config;
    }

    @Override
    public NettyClientChannel connect(String host, int port) {
        if (started.compareAndSet(false, true)) {
            int workerThreadNumber = ReadUtils
                    .getOrDefault(config.getWorkerThreadNumber(), DEFAULT_WORKER_THREAD_NUMBER);

            workerGroup = Objects.nonNull(workerGroup) ? workerGroup : new NioEventLoopGroup(workerThreadNumber);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap = bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientChannelInitializer());
            clientBootstrap = bootstrap;
        }
        ChannelFuture channelFuture = clientBootstrap.connect(host, port);
        try {
            boolean result = channelFuture.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = channelFuture.channel();
        return NettyClientChannel.of(channel);
    }

    public static class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(OutboundExceptionHandler.INSTANCE);
            pipeline.addLast(new RpcDecoder());
            pipeline.addLast(new RpcEncoder());
            pipeline.addLast(InboundExceptionHandler.INSTANCE);
        }
    }

}
