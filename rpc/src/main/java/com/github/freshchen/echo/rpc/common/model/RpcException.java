package com.github.freshchen.echo.rpc.common.model;

/**
 * @author darcy
 * @since 2022/04/09
 **/
public class RpcException extends RuntimeException {
    private static final long serialVersionUID = -1261538095411686141L;

    public RpcException(String msg, Throwable e) {
        super(msg, e);
    }

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Throwable e) {
        super(e);
    }
}
