package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.client.model.RpcClientContext;
import com.github.freshchen.echo.rpc.client.model.RpcFuture;
import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.protocol.Protocol;
import com.github.freshchen.echo.rpc.protocol.RpcProto;
import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.model.MethodInfo;
import com.github.freshchen.echo.rpc.registry.model.ReferenceInfo;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;
import com.github.freshchen.echo.rpc.transport.netty.client.channel.PooledClientChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author darcy
 * @since 2022/04/11
 **/
@Slf4j
@Getter
public class RpcReferenceHandler {

    private ReferenceInfo referenceInfo;
    private Registry registry;
    private Protocol protocol;
    private ServiceInfo serviceInfo;

    private PooledClientChannel pooledClientChannel;

    private AtomicLong correlationIdAdder;

    public static RpcReferenceHandler init(Registry registry,
                                           Protocol protocol,
                                           ReferenceInfo referenceInfo) {
        ServiceInfo serviceInfo = registry.discover(referenceInfo);
        RpcReferenceHandler rpcReferenceClient = new RpcReferenceHandler();
        rpcReferenceClient.referenceInfo = referenceInfo;
        rpcReferenceClient.registry = registry;
        rpcReferenceClient.protocol = protocol;
        rpcReferenceClient.serviceInfo = serviceInfo;
        rpcReferenceClient.pooledClientChannel = new PooledClientChannel(serviceInfo);
        rpcReferenceClient.correlationIdAdder = new AtomicLong(System.currentTimeMillis());
        return rpcReferenceClient;
    }

    public Object sendRequest(Method method, Object[] args) {
        MethodInfo methodInfo = getReferenceInfo().getMethodInfoMap().get(method);
        if (Objects.isNull(methodInfo)) {
            return null;
        }
        long correlationId = correlationIdAdder.incrementAndGet();
        RpcProto.Package requestPackage = protocol.createRequestPackage(correlationId, methodInfo, args);
        RpcFuture rpcFuture = protocol.createRequestRpcFuture(correlationId);

        RpcClientContext.putRpcFuture(rpcFuture);

        Channel channel = pooledClientChannel.getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(requestPackage);
        returnChannelAfterRequest(pooledClientChannel, channel);

        boolean success = channelFuture.awaitUninterruptibly(serviceInfo.getSendRequestTimeoutMillis());
        if (!success || !channelFuture.isSuccess()) {
            handleSendRequestError(correlationId, pooledClientChannel, channel);
            String errMsg = String.format("send request failed, channelIsActive=%b",
                    channel.isActive());
            Throwable cause = channelFuture.cause();
            if (cause != null) {
                throw new RpcException(errMsg, cause);
            }
            throw new RpcException(errMsg);
        }

        try {
            return rpcFuture.;
        } catch (Exception e) {
            throw new RpcException("get response error", e);
        }
    }

    private void handleSendRequestError(long correlationId, PooledClientChannel pool, Channel channel) {
        RpcFuture removedRpcFuture = RpcClientContext.remove(correlationId);
        removedRpcFuture.cancel(true);
    }

    private void returnChannelAfterRequest(PooledClientChannel pool, Channel channel) {
        pool.returnChannel(channel);
    }

}
