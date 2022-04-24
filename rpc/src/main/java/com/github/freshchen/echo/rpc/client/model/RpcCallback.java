package com.github.freshchen.echo.rpc.client.model;

/**
 * @author darcy
 * @since 2022/04/16
 **/
public interface RpcCallback<T> {

    void success(T response);
}
