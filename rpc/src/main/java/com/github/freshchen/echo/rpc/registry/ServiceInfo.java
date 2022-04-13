package com.github.freshchen.echo.rpc.registry;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.server.annotation.RpcService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@SuperBuilder
public class ServiceInfo extends Endpoint {

    private String id;
    private String applicationName;
    private String className;
    private Class<?> interfaceClass;

    public static ServiceInfo of(RpcService rpcService, Class<?> clz) {
        String clzName = clz.getName();
        return ServiceInfo.builder()
                .id(ReadUtils.getOrDefault(rpcService.id(), clzName))
                .className(clzName)
                .interfaceClass(clz)
                .build();
    }

}
