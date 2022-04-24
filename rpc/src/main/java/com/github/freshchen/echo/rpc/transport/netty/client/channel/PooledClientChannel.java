package com.github.freshchen.echo.rpc.transport.netty.client.channel;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.NoSuchElementException;

/**
 * @author darcy
 * @since 2022/04/17
 **/
@Slf4j
public class PooledClientChannel {

    private ServiceInfo serviceInfo;
    private GenericObjectPool<Channel> channelPool;

    public PooledClientChannel(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
        PooledObjectFactory<Channel> objectFactory = new ChannelPooledObjectFactory(serviceInfo);
        channelPool = new GenericObjectPool<>(objectFactory, serviceInfo.getClientChannelConfig());
        try {
            channelPool.preparePool();
        } catch (Exception ex) {
            log.warn("prepare min idle channel pool failed");
        }
    }

    public Channel getChannel() {
        Channel channel;
        try {
            channel = channelPool.borrowObject();
        } catch (NoSuchElementException full) {
            throw new RpcException("channel pool is fulled", full);
        } catch (IllegalStateException illegalState) {
            throw new RpcException("channel is closed", illegalState);
        } catch (Exception connectedFailed) {
            throw new RpcException("get channel failed", connectedFailed);
        }

        if (channel == null) {
            throw new RpcException("get channel failed");
        }
        if (!channel.isActive()) {
            removeChannel(channel);
            throw new RpcException("channel is non active");
        }
        return channel;
    }

    public void returnChannel(Channel channel) {
        channelPool.returnObject(channel);
    }

    public void removeChannel(Channel channel) {
        try {
            channelPool.invalidateObject(channel);
        } catch (Exception e) {
            log.debug("remove channel failed:{}", e.getMessage());
        }
    }

    public void close() {
        channelPool.close();
    }

}
