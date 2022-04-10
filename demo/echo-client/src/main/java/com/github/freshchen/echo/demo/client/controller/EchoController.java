package com.github.freshchen.echo.demo.client.controller;

import com.github.freshchen.echo.demo.api.EchoService;
import com.github.freshchen.echo.rpc.client.annotation.RpcReference;
import com.github.freshchen.echo.rpc.transport.Client;
import com.github.freshchen.echo.rpc.transport.RpcProtocol;
import com.github.freshchen.echo.rpc.transport.netty.client.NettyClientChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@RestController
@Slf4j
public class EchoController {

    @Autowired
    private Client client;

    @RpcReference
    private EchoService echoService;


    @GetMapping("/echo")
    public Result echo() throws InterruptedException, ExecutionException {
        NettyClientChannel connect = client.connect("127.0.0.1", 8888);

        RpcProtocol rpcPacket = RpcProtocol.builder().data("echo213123123123".getBytes(StandardCharsets.UTF_8)).build();
        Channel channel = connect.getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(rpcPacket);
        boolean result = channelFuture.await(5000, TimeUnit.MILLISECONDS);
        String echo = echoService.echo("1");
        return Result.of("echo");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Result {
        private String data;
    }

}
