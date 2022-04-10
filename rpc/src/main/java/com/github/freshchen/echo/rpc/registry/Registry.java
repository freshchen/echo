package com.github.freshchen.echo.rpc.registry;

import java.util.Map;
import java.util.Set;

/**
 * @author darcy
 * @since 2022/04/10
 **/
public interface Registry {

    ServiceInfo discover(ServiceInfo info);

    boolean register(ServiceInfo info);

    Map<ServiceInfo, Boolean> register(Set<ServiceInfo> infos);
}
