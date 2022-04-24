package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import com.github.freshchen.echo.rpc.common.util.ProxyFactory;
import com.github.freshchen.echo.rpc.protocol.Protocol;
import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.model.ReferenceInfo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.NOT_PROXY_METHOD_SET;
import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.TO_STRING;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@NoArgsConstructor
@Slf4j
public class RpcReferenceProxy implements InvocationHandler {

    private BeanFactory beanFactory;
    private Class<?> interfaceClass;
    private String serviceName;
    private String applicationName;
    private RpcReferenceHandler referenceHandler;

    public static <T> T createRpcReferenceProxy(String serviceName,
                                                Class<T> interfaceClass,
                                                String applicationName,
                                                BeanFactory beanFactory) {
        Asserts.notNull(interfaceClass);
        Asserts.notNull(beanFactory);

        Registry registry = beanFactory.getBean(Registry.class);
        Protocol protocol = beanFactory.getBean(Protocol.class);

        RpcReferenceProxy proxy = new RpcReferenceProxy();
        proxy.serviceName = serviceName;
        proxy.applicationName = applicationName;
        proxy.interfaceClass = interfaceClass;
        ReferenceInfo referenceInfo = ReferenceInfo.of(serviceName, applicationName, interfaceClass);
        proxy.referenceHandler = RpcReferenceHandler.init(registry, protocol, referenceInfo);

        return ProxyFactory.createProxy(interfaceClass, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (NOT_PROXY_METHOD_SET.contains(methodName)) {
            if (TO_STRING.equals(methodName)) {
                return "RpcReferenceProxy:" + interfaceClass.getName();
            }
            if (log.isWarnEnabled()) {
                log.warn("method [{}] not supported", methodName);
            }
            return null;
        }
        return referenceHandler.sendRequest(method, args);
    }

}
