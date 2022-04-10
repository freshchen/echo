package com.github.freshchen.echo.rpc.config;

import com.github.freshchen.echo.rpc.server.RpcServiceHandler;
import com.github.freshchen.echo.rpc.transport.Server;
import com.github.freshchen.echo.rpc.transport.netty.server.NettyServer;
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
@ConditionalOnProperty(value = "rpc.server.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RpcServerConfiguration.Config.class)
public class RpcServerConfiguration {

    @Bean
    public RpcServiceHandler rpcServiceHandler() {
        return new RpcServiceHandler();
    }

    @ConditionalOnProperty(value = "rpc.server.impl", havingValue = "netty", matchIfMissing = true)
    @EnableConfigurationProperties(NettyServerConfiguration.Config.class)
    public static class NettyServerConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Server nettyServer(Config config,
                                  RpcServerConfiguration.Config serverConfig) {
            return new NettyServer(config, serverConfig);
        }

        @Data
        @ConfigurationProperties(prefix = "rpc.server.netty.config")
        public static class Config {
            private Integer workerThreadNumber;
            private Integer bossIoRatio;
        }

    }

    @Data
    @ConfigurationProperties(prefix = "rpc.server")
    public static class Config {
        private String impl;
        private Integer port;
    }

}
