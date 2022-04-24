package com.github.freshchen.echo.rpc.config;

import com.github.freshchen.echo.rpc.client.annotation.RpcReferencePostProcessor;
import com.github.freshchen.echo.rpc.protocol.Protocol;
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
@EnableConfigurationProperties(RpcClientConfiguration.Config.class)
public class RpcClientConfiguration {

    @Bean
    public RpcReferencePostProcessor rpcReferencePostProcessor() {
        return new RpcReferencePostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyClient nettyClient(Config config) {
        return new NettyClient(config);
    }


    @Bean
    public Protocol protocol() {
        return new Protocol();
    }

    @Data
    @ConfigurationProperties(prefix = "rpc.client.netty.config")
    public static class Config {
        private Integer workerThreadNumber;
    }

}
