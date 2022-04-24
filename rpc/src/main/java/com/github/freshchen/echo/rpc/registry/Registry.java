package com.github.freshchen.echo.rpc.registry;

import com.github.freshchen.echo.rpc.registry.model.ReferenceInfo;
import com.github.freshchen.echo.rpc.registry.model.ServiceInfo;

import java.util.Map;
import java.util.Set;

/**
 * @author darcy
 * @since 2022/04/10
 **/
public interface Registry {

    ServiceInfo discover(ReferenceInfo info);

    boolean register(ReferenceInfo info);

    Map<ReferenceInfo, Boolean> register(Set<ReferenceInfo> infos);
}
