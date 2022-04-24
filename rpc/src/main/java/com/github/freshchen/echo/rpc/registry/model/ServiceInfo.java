package com.github.freshchen.echo.rpc.registry.model;

import com.github.freshchen.echo.rpc.common.util.ReadUtils;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

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

    @Builder.Default
    private int sendRequestTimeoutMillis = 1000;

    @Builder.Default
    private int getResponseTimeoutMillis = 2000;

    private ClientBootstrapConfig clientBootstrapConfig;
    private GenericObjectPoolConfig<Channel> clientChannelConfig;

    public static ServiceInfo of(String id, String applicationName, Class<?> interfaceClass) {
        String clzName = interfaceClass.getName();
        return ServiceInfo.builder()
                .id(ReadUtils.getOrDefault(id, clzName))
                .applicationName(applicationName)
                .className(clzName)
                .interfaceClass(interfaceClass)
                .build();
    }

    @Override
    public String toString() {
        return String.format("%s[%s:%d]", applicationName, getHost(), getPort());
    }

}
