package com.github.freshchen.echo.rpc.registry.mock;

import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.ServiceInfo;

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
    public ServiceInfo discover(ServiceInfo info) {
        return ServiceInfo.builder()
                .id("/mock/service")
                .host("127.0.0.1")
                .port(8888)
                .build();
    }

    @Override
    public boolean register(ServiceInfo info) {
        return true;
    }

    @Override
    public Map<ServiceInfo, Boolean> register(Set<ServiceInfo> infos) {
        return infos.stream()
                .collect(Collectors.toMap(Function.identity(), v -> Boolean.TRUE));
    }
}
