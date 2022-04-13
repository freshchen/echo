package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import com.github.freshchen.echo.rpc.registry.Registry;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.NOT_PROXY_METHOD_SET;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@NoArgsConstructor
public class RpcReferenceProxy implements InvocationHandler {

    private Class<?> interfaceClass;
    private String id;
    private String applicationName;
    private Registry registry;

    private RpcReferenceClient client;


    public static RpcReferenceProxy createRpcReferenceProxy(String id,
                                                            Class<?> interfaceClass,
                                                            String applicationName,
                                                            Registry registry) {
        Asserts.notBlank(id);
        Asserts.notBlank(applicationName);
        Asserts.notNull(interfaceClass);
        Asserts.notNull(registry);

        RpcReferenceProxy proxy = new RpcReferenceProxy();
        proxy.id = id;
        proxy.applicationName = applicationName;
        proxy.interfaceClass = interfaceClass;
        proxy.registry = registry;
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (NOT_PROXY_METHOD_SET.contains(methodName)) {
            if ("toString".equals(methodName)) {
                return "RpcReferenceMethodHandler:" + client.getInterfaceClass();
            }
            return null;
        }
        return client.invoke(method, args);
    }

}
