package com.github.freshchen.echo.rpc.proxy;

/**
 * @author darcy
 * @since 2022/04/09
 **/
public interface ProxyFactory {

    <T> T createProxy(Class<T> type, Object handler);

}
