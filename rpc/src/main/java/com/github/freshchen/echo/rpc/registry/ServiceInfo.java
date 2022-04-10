package com.github.freshchen.echo.rpc.registry;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import com.github.freshchen.echo.rpc.server.annotation.RpcService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ServiceInfo {

    private String id;
    private String host;
    private int port;
    private String className;

    public static ServiceInfo of(RpcService rpcService, Class<?> clz) {
        String clzName = clz.getName();
        return ServiceInfo.builder()
                .id(ReadUtils.getOrDefault(rpcService.id(), clzName))
                .className(clzName)
                .build();
    }

}
