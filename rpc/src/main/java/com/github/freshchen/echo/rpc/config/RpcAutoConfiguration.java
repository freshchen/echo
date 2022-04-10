package com.github.freshchen.echo.rpc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Configuration(proxyBeanMethods = false)
@Import({
        RpcServerConfiguration.class,
        RpcClientConfiguration.class,
        RpcRegistryConfiguration.class
})
public class RpcAutoConfiguration {
}
