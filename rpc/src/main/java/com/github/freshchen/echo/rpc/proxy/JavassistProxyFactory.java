package com.github.freshchen.echo.rpc.proxy;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Slf4j
public class JavassistProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> type, Object handler) {
        try {
            MethodHandler methodHandler = (MethodHandler) handler;
            javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
            boolean isInterface = type.isInterface();
            if (isInterface) {
                proxyFactory.setInterfaces(new Class[]{type});
            } else {
                proxyFactory.setSuperclass(type);
            }
            Class<?> clz = proxyFactory.createClass();
            Object proxy = clz.newInstance();
            ((ProxyObject) proxy).setHandler(methodHandler);
            return (T) proxy;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException("javassist create proxy error", e);
        }
    }

}
