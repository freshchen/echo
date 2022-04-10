package com.github.freshchen.echo.rpc.config;

import com.github.freshchen.echo.rpc.client.RpcReferenceHandler;
import com.github.freshchen.echo.rpc.transport.Client;
import com.github.freshchen.echo.rpc.transport.netty.client.NettyClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "rpc.client.enabled", havingValue = "true", matchIfMissing = true)
public class RpcClientConfiguration {

    @Bean
    public RpcReferenceHandler rpcReferenceHandler() {
        return new RpcReferenceHandler();
    }

    @ConditionalOnProperty(value = "rpc.client.impl", havingValue = "netty", matchIfMissing = true)
    @EnableConfigurationProperties(NettyClientConfiguration.Config.class)
    public static class NettyClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Client nettyClient(Config config) {
            return new NettyClient(config);
        }

        @Data
        @ConfigurationProperties(prefix = "rpc.client.netty.config")
        public static class Config {
            private Integer workerThreadNumber;
        }

    }

}
