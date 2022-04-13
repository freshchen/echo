package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author darcy
 * @since 2022/04/11
 **/
@Data
public class RpcReferenceClient {

    private String id;
    private Class<?> interfaceClass;
    private Registry registry;
    private ServiceInfo serviceInfo;
    private Bootstrap bootstrap;

    public void setId(String id) {
        this.id = id;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Object invoke(Method method, Object[] args) {
        return null;
    }
}
