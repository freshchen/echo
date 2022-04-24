package com.github.freshchen.echo.rpc.transport.netty.client.channel;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;
import com.github.freshchen.echo.rpc.transport.netty.client.bootstrap.ClientBootstrapManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author darcy
 * @since 2022/04/17
 **/
@Slf4j
public class ChannelPooledObjectFactory extends BasePooledObjectFactory<Channel> {

    private ServiceInfo serviceInfo;

    public ChannelPooledObjectFactory(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    @Override
    public Channel create() throws Exception {
        final String host = serviceInfo.getHost();
        final int port = serviceInfo.getPort();
        Bootstrap bootstrap = ClientBootstrapManager.getInstance()
                .takeClientBootstrap(serviceInfo.getClientBootstrapConfig());
        final ChannelFuture future = bootstrap.connect(host, port);
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.debug("connect to {}:{} success, channel={}",
                        host, port, channelFuture.channel());
            } else {
                log.debug("connect to {}:{} failed due to {}",
                        host, port, channelFuture.cause().getMessage());
            }
        });
        future.syncUninterruptibly();
        if (future.isSuccess()) {
            log.info("connect to {} success", serviceInfo);
            return future.channel();
        } else {
            log.error("connect to {}:{} failed, msg={}", host, port, future.cause().getMessage());
            throw new RpcException(future.cause());
        }
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
        Channel channel = pooledObject.getObject();
        if (channel != null && channel.isOpen() && channel.isActive()) {
            channel.close();
        }
        channel = null;
    }

    public boolean validateObject(PooledObject<Channel> p) {
        Channel channel = p.getObject();
        return channel != null && channel.isOpen() && channel.isActive();
    }

}
