package com.github.freshchen.echo.rpc.transport.netty.server;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.config.RpcServerConfiguration;
import com.github.freshchen.echo.rpc.server.RpcServiceHandler;
import com.github.freshchen.echo.rpc.transport.netty.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.freshchen.echo.rpc.common.constant.RpcNettyConstants.DEFAULT_SO_BACKLOG;
import static com.github.freshchen.echo.rpc.common.constant.RpcNettyConstants.DEFAULT_WORKER_THREAD_NUMBER;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
public class NettyServer {

    private static AtomicBoolean started = new AtomicBoolean(false);

    private final RpcServerConfiguration.Config config;
    private final RpcServiceHandler rpcServiceHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Channel serverChannel;

    public NettyServer(RpcServerConfiguration.Config config,
                       RpcServiceHandler rpcServiceHandler) {
        this.config = config;
        this.rpcServiceHandler = rpcServiceHandler;
    }

    public boolean start() {
        if (started.compareAndSet(false, true)) {
            int workerThreadNumber = ReadUtils
                    .getOrDefault(config.getWorkerThreadNumber(), DEFAULT_WORKER_THREAD_NUMBER);
            int bossIoRatio = ReadUtils.getOrDefault(config.getBossIoRatio(), 50);
            int port = ReadUtils.getOrDefault(config.getPort(), 8800);

            bossGroup = Objects.nonNull(bossGroup) ? bossGroup : new NioEventLoopGroup(1);
            ((NioEventLoopGroup) bossGroup).setIoRatio(bossIoRatio);
            workerGroup = Objects.nonNull(workerGroup) ? workerGroup : new NioEventLoopGroup(workerThreadNumber);

            this.serverBootstrap = new ServerBootstrap();

            ChannelInitializer<SocketChannel> serverChannelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(OutboundExceptionHandler.INSTANCE);
                    pipeline.addLast(new RpcDecoder());
                    pipeline.addLast(new RpcEncoder());
                    pipeline.addLast(new ServerInboundHandler(rpcServiceHandler));
                    pipeline.addLast(InboundExceptionHandler.INSTANCE);
                }
            };

            ChannelFuture channelFuture = serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_BACKLOG, DEFAULT_SO_BACKLOG)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(serverChannelInitializer)
                    .bind(port)
                    .syncUninterruptibly();
            serverChannel = channelFuture.channel();
            log.info("NettyServer is started on port: {}", port);
        }
        return started.get();
    }

}
