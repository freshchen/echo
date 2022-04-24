package com.github.freshchen.echo.rpc.transport.netty.client;

import com.github.freshchen.echo.rpc.registry.model.ClientBootstrapConfig;
import com.github.freshchen.echo.rpc.transport.netty.client.bootstrap.ClientBootstrapManager;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
class ClientBootstrapManagerTest {

    @org.junit.jupiter.api.Test
    void takeClientBootstrap() {

        System.out.println("service 1");
        ClientBootstrapConfig config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .serviceIoThreadNum(1).serviceName("getOrder").build();
        print(config);

        System.out.println("service 2");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .serviceIoThreadNum(2).serviceName("getOrder").build();
        print(config);

        System.out.println("service 3");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .serviceIoThreadNum(2).serviceName("getOrder").build();
        print(config);

        System.out.println("application 1");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .applicationIoThreadNum(1).applicationName("order").build();
        print(config);

        System.out.println("application 2");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .applicationIoThreadNum(2).applicationName("order").build();
        print(config);

        System.out.println("application 3");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .applicationIoThreadNum(2).applicationName("order").build();
        print(config);

        System.out.println("shared 1");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .sharedIoThreadNum(1).build();
        print(config);

        System.out.println("shared 2");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .sharedIoThreadNum(2).build();
        print(config);

        System.out.println("shared 3");
        config = ClientBootstrapConfig.builder().timestamp(System.currentTimeMillis())
                .sharedIoThreadNum(2).build();
        print(config);


    }

    private void print(ClientBootstrapConfig config) {
        EventLoopGroup eventLoopGroup = ClientBootstrapManager.getInstance().takeClientBootstrap(config).config().group();
        IntStream.rangeClosed(0, 5).forEach(index -> {
            eventLoopGroup.submit(() -> {
                try {
                    log.info("hello" + index);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
