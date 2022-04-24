package com.github.freshchen.echo.rpc.registry.model;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.protocol.RpcProto;
import com.github.freshchen.echo.rpc.protocol.RpcProtoUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.NOT_PROXY_METHOD_SET;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ReferenceInfo extends Endpoint {

    private String serviceName;
    private String applicationName;
    private String className;
    private Class<?> interfaceClass;

    private Map<Method, MethodInfo> methodInfoMap;

    public static ReferenceInfo of(String serviceName, String applicationName, Class<?> interfaceClass) {
        String clzName = interfaceClass.getName();
        Method[] methods = interfaceClass.getMethods();
        Map<Method, MethodInfo> methodInfoMap = Stream.of(methods)
                .map(method -> {
                    String methodName = method.getName();
                    if (NOT_PROXY_METHOD_SET.contains(methodName)) {
                        return null;
                    }
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    int paramLength = parameterTypes.length;
                    if (paramLength == 0) {
                        return MethodInfo.builder().method(method).serviceName(serviceName).methodName(methodName).isSync(true).hasArg(false).build();
                    }
                    Class<?>[] actualParameterTypes = new Class[paramLength];
                    RpcProto.DataSerializeType[] parameterSerializeTypes = new RpcProto.DataSerializeType[paramLength];
                    for (int i = 0; i < paramLength; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        parameterSerializeTypes[i] = RpcProtoUtils.getSerializeType(parameterType);
                        actualParameterTypes[i] = parameterType;
                    }
                    return MethodInfo.builder().method(method).serviceName(serviceName).methodName(methodName).isSync(true).hasArg(true)
                            .actualParameterTypes(actualParameterTypes)
                            .parameterSerializeTypes(parameterSerializeTypes)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(MethodInfo::getMethod, Function.identity()));
        return ReferenceInfo.builder()
                .serviceName(ReadUtils.getOrDefault(serviceName, clzName))
                .applicationName(applicationName)
                .className(clzName)
                .interfaceClass(interfaceClass)
                .methodInfoMap(methodInfoMap)
                .build();
    }
}
