package com.github.freshchen.echo.rpc.registry.mock;

import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.model.ClientBootstrapConfig;
import com.github.freshchen.echo.rpc.registry.model.ReferenceInfo;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;
import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author darcy
 * @since 2022/04/10
 **/
public class MockRegistry implements Registry {

    @Override
    public ServiceInfo discover(ReferenceInfo info) {
        GenericObjectPoolConfig<Channel> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(8);
        return ServiceInfo.builder()
                .id("/mock/service")
                .host("127.0.0.1")
                .applicationName("order")
                .port(8888)
                .clientBootstrapConfig(ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                        .applicationIoThreadNum(2).applicationName("order").build())
                .clientChannelConfig(poolConfig)
                .build();
    }

    @Override
    public boolean register(ReferenceInfo info) {
        return true;
    }

    @Override
    public Map<ReferenceInfo, Boolean> register(Set<ReferenceInfo> infos) {
        return infos.stream()
                .collect(Collectors.toMap(Function.identity(), v -> Boolean.TRUE));
    }
}
