package com.github.freshchen.echo.rpc.client.model;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.protocol.RpcProto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@Slf4j
public class RpcFuture implements Future<RpcProto.Package> {
    @Getter
    private long correlationId;
    private boolean isDone;

    @Setter
    private CountDownLatch latch;
    private RpcProto.Package response;

    public RpcFuture(long correlationId) {
        this.correlationId = correlationId;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public RpcProto.Package get() throws InterruptedException, ExecutionException {
        latch.await();
        return response;
    }

    @Override
    public RpcProto.Package get(long timeout, TimeUnit unit) {
        try {
            boolean success = latch.await(timeout, unit);
            if (!success) {
                throw new RpcException("response timeout");
            }
            assert response != null;
            if (response.getException() != null) {
                throw new RpcException(response.getException());
            }
            setRpcContext();
            return (T) response.getResult();
        } catch (InterruptedException e) {
            throw new RpcException(RpcException.UNKNOWN_EXCEPTION, e);
        }
    }

    public void setResponse(RpcProto.Package response) {
        this.response = response;
        latch.countDown();
    }


    @Override
    public String toString() {
        return "RpcFuture correlationId: " + correlationId;
    }
}
