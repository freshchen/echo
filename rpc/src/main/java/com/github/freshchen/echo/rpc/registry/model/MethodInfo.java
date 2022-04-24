package com.github.freshchen.echo.rpc.registry.model;

import com.github.freshchen.echo.rpc.protocol.RpcProto;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@Builder
@Getter
public class MethodInfo {

    private Method method;
    private String serviceName;
    private String methodName;
    private boolean isSync;
    private boolean hasArg;
    private Class<?>[] actualParameterTypes;
    private RpcProto.DataSerializeType[] parameterSerializeTypes;

}
