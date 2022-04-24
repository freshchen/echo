package com.github.freshchen.echo.rpc.transport.netty.client;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.config.RpcClientConfiguration;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;
import com.github.freshchen.echo.rpc.transport.netty.handler.*;
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
public class NettyClient {

    private static AtomicBoolean started = new AtomicBoolean(false);

    private RpcClientConfiguration.Config config;

    private EventLoopGroup workerGroup;
    private Bootstrap sharedBootstrap;

    public NettyClient(RpcClientConfiguration.Config config) {
        this.config = config;
        if (started.compareAndSet(false, true)) {
            int workerThreadNumber = ReadUtils
                    .getOrDefault(config.getWorkerThreadNumber(), DEFAULT_WORKER_THREAD_NUMBER);

            workerGroup = Objects.nonNull(workerGroup) ? workerGroup : new NioEventLoopGroup(workerThreadNumber);

            Bootstrap bootstrap = new Bootstrap();

            ChannelInitializer<SocketChannel> clientChannelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(OutboundExceptionHandler.INSTANCE);
                    pipeline.addLast(new RpcDecoder());
                    pipeline.addLast(new RpcEncoder());
                    pipeline.addLast(ClientInboundHandler.INSTANCE);
                    pipeline.addLast(InboundExceptionHandler.INSTANCE);

                }
            };

            bootstrap = bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(clientChannelInitializer);
            sharedBootstrap = bootstrap;
        }
    }

    public Bootstrap getBootstrap(ServiceInfo serviceInfo) {
        return sharedBootstrap;
    }

    public Channel connect(String host, int port) {

        ChannelFuture channelFuture = sharedBootstrap.connect(host, port);
        try {
            boolean result = channelFuture.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channelFuture.channel();
    }

}
