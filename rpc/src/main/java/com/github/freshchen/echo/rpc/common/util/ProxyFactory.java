package com.github.freshchen.echo.rpc.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author darcy
 * @since 2022/04/09
 **/
public class ProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type},
                handler);
    }

}
