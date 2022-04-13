package com.github.freshchen.echo.rpc.config;

import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.mock.MockRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "rpc.registry.enabled", havingValue = "true", matchIfMissing = true)
public class RpcRegistryConfiguration {

    @ConditionalOnProperty(value = "rpc.registry.impl", havingValue = "mock")
    public static class NettyClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Registry mockRegistry() {
            return new MockRegistry();
        }

    }

}
