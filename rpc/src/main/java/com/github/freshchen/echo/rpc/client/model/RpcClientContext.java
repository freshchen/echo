package com.github.freshchen.echo.rpc.client.model;

import com.github.freshchen.echo.rpc.protocol.RpcProto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcClientContext {

    private static final Map<Long, RpcFuture> CORRELATION_ID_RPC_FUTURE_MAP = new ConcurrentHashMap<>();


    public static void putRpcFuture(RpcFuture rpcFuture) {
        CORRELATION_ID_RPC_FUTURE_MAP.put(rpcFuture.getCorrelationId(), rpcFuture);
    }

    public static void setResponse(RpcProto.Package response) {
        long correlationId = response.getCorrelationId();
        RpcFuture future = CORRELATION_ID_RPC_FUTURE_MAP.get(correlationId);
        future.setResponse(response);
    }

    public static RpcFuture remove(long correlationId) {
       return CORRELATION_ID_RPC_FUTURE_MAP.remove(correlationId);
    }
}
