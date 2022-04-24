package com.github.freshchen.echo.rpc.transport.netty.client.bootstrap;

import com.github.freshchen.echo.rpc.registry.model.ClientBootstrapConfig;
import com.github.freshchen.echo.rpc.common.annotation.Shutdown;
import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.transport.netty.handler.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@Slf4j
public class ClientBootstrapManager implements Shutdown {
    private volatile ConfigurableClientBootstrap sharedClientBootstrap;
    private final Map<String, ConfigurableClientBootstrap> serviceIsolationClientBootstrap = new ConcurrentHashMap<>();
    private final Map<String, ConfigurableClientBootstrap> applicationIsolationClientBootstrap = new ConcurrentHashMap<>();

    private static volatile ClientBootstrapManager instance;

    public static ClientBootstrapManager getInstance() {
        if (instance == null) {
            synchronized (ClientBootstrapManager.class) {
                if (instance == null) {
                    instance = new ClientBootstrapManager();
                }
            }
        }
        return instance;
    }

    public Bootstrap takeClientBootstrap(ClientBootstrapConfig config) {
        ClientBootstrapConfig.Type type = config.checkAndGetType();
        long timestamp = config.getTimestamp();
        if (ClientBootstrapConfig.Type.SERVICE.equals(type)) {
            String serviceName = config.getServiceName();
            int serviceIoThreadNum = config.getServiceIoThreadNum();

            ConfigurableClientBootstrap configurable;
            if ((configurable = serviceIsolationClientBootstrap.get(serviceName)) == null) {
                synchronized (serviceName.intern()) {
                    if ((configurable = serviceIsolationClientBootstrap.get(serviceName)) == null) {
                        configurable = ConfigurableClientBootstrap
                                .create(serviceIoThreadNum,
                                        String.format(type.getThreadNamePrefix(), serviceName),
                                        timestamp);
                        ConfigurableClientBootstrap prev = serviceIsolationClientBootstrap.putIfAbsent(serviceName, configurable);
                        if (prev != null) {
                            log.warn("repeat config:{} serviceName: {}", config, serviceName);
                            prev.getEventLoopGroup().shutdownGracefully().awaitUninterruptibly();
                        }
                    }
                }
            }
            if (configurable.needRefresh(serviceIoThreadNum, timestamp)) {
                return configurable.refreshBootstrap(serviceIoThreadNum, timestamp);
            }
            return configurable.getBootstrap();
        }

        if (ClientBootstrapConfig.Type.APPLICATION.equals(type)) {
            String applicationName = config.getApplicationName();
            int applicationIoThreadNum = config.getApplicationIoThreadNum();

            ConfigurableClientBootstrap configurable;
            if ((configurable = applicationIsolationClientBootstrap.get(applicationName)) == null) {
                synchronized (applicationName.intern()) {
                    if ((configurable = applicationIsolationClientBootstrap.get(applicationName)) == null) {
                        configurable = ConfigurableClientBootstrap
                                .create(applicationIoThreadNum,
                                        String.format(type.getThreadNamePrefix(), applicationName),
                                        timestamp);
                        ConfigurableClientBootstrap prev = applicationIsolationClientBootstrap.putIfAbsent(applicationName, configurable);
                        if (prev != null) {
                            log.warn("repeat config:{} applicationName: {}", config, applicationName);
                            prev.getEventLoopGroup().shutdownGracefully().awaitUninterruptibly();
                        }
                    }
                }
            }
            if (configurable.needRefresh(applicationIoThreadNum, timestamp)) {
                return configurable.refreshBootstrap(applicationIoThreadNum, timestamp);
            }
            return configurable.getBootstrap();
        }

        if (ClientBootstrapConfig.Type.SHARED.equals(type)) {
            int sharedIoThreadNum = config.getSharedIoThreadNum();

            if (sharedClientBootstrap == null) {
                synchronized (ClientBootstrapManager.class) {
                    if (sharedClientBootstrap == null) {
                        sharedClientBootstrap = ConfigurableClientBootstrap.create(sharedIoThreadNum,
                                type.getThreadNamePrefix(),
                                timestamp);
                    }
                }
            }
            if (sharedClientBootstrap.needRefresh(sharedIoThreadNum, timestamp)) {
                return sharedClientBootstrap.refreshBootstrap(sharedIoThreadNum, timestamp);
            }
            return sharedClientBootstrap.getBootstrap();
        }

        throw new RpcException("ClientEventLoopGroupConfig Error");
    }

    @Override
    public void shutdown() {
        if (Objects.nonNull(sharedClientBootstrap)) {
            sharedClientBootstrap.getEventLoopGroup().shutdownGracefully().awaitUninterruptibly();
            sharedClientBootstrap = null;
        }
        if (MapUtils.isNotEmpty(serviceIsolationClientBootstrap)) {
            serviceIsolationClientBootstrap.forEach((k, v) -> v.getEventLoopGroup().shutdownGracefully().awaitUninterruptibly());
            serviceIsolationClientBootstrap.clear();
        }
        if (MapUtils.isNotEmpty(applicationIsolationClientBootstrap)) {
            applicationIsolationClientBootstrap.forEach((k, v) -> v.getEventLoopGroup().shutdownGracefully().awaitUninterruptibly());
            applicationIsolationClientBootstrap.clear();
        }
    }

    @Getter
    @Slf4j
    public static class ConfigurableClientBootstrap {
        private int curThreadNum;
        private String threadNamePrefix;
        private EventLoopGroup eventLoopGroup;
        private Bootstrap bootstrap;
        private long lastTimestamp;

        public static ConfigurableClientBootstrap create(int threadNum, String threadNamePrefix, long timestamp) {
            ConfigurableClientBootstrap group = new ConfigurableClientBootstrap();
            group.lastTimestamp = timestamp;
            group.curThreadNum = threadNum;
            group.threadNamePrefix = threadNamePrefix;
            // CustomizableThreadFactory 不能复用，不然线程 count 会不断累加
            group.eventLoopGroup = new NioEventLoopGroup(threadNum, new CustomizableThreadFactory(threadNamePrefix));
            group.bootstrap = group.createBootstrap(group.eventLoopGroup);
            return group;
        }

        public boolean needRefresh(int threadNum, long timestamp) {
            return threadNum != curThreadNum && timestamp > lastTimestamp;
        }

        public Bootstrap refreshBootstrap(int threadNum, long timestamp) {
            if (needRefresh(threadNum, timestamp)) {
                synchronized (ConfigurableClientBootstrap.class) {
                    EventLoopGroup oldGroup = this.eventLoopGroup;
                    int oldNum = this.curThreadNum;
                    this.lastTimestamp = timestamp;
                    this.curThreadNum = threadNum;
                    this.eventLoopGroup = new NioEventLoopGroup(this.curThreadNum, new CustomizableThreadFactory(threadNamePrefix));
                    oldGroup.shutdownGracefully().awaitUninterruptibly();
                    this.bootstrap = createBootstrap(this.eventLoopGroup);
                    log.info("{} thead num from {} to {}", threadNamePrefix, oldNum, this.curThreadNum);
                }
            }
            return bootstrap;
        }

        private Bootstrap createBootstrap(EventLoopGroup eventLoopGroup) {
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

            return bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(clientChannelInitializer);
        }


    }
}
