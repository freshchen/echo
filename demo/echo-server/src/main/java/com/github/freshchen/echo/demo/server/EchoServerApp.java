package com.github.freshchen.echo.demo.server;

import com.github.freshchen.echo.rpc.transport.netty.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@SpringBootApplication
public class EchoServerApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EchoServerApp.class);

        NettyServer server = context.getBean(NettyServer.class);
        server.start();
    }
}
