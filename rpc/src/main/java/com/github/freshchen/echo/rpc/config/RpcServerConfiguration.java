package com.github.freshchen.echo.rpc.config;

import com.github.freshchen.echo.rpc.server.RpcServiceHandler;
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

    @Bean
    @ConditionalOnMissingBean
    public NettyServer nettyServer(Config config) {
        return new NettyServer(config);
    }

    @Data
    @ConfigurationProperties(prefix = "rpc.server")
    public static class Config {
        private Integer port;
        private Integer workerThreadNumber;
        private Integer bossIoRatio;
    }

}
