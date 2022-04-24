package com.github.freshchen.echo.demo.client.controller;

import com.github.freshchen.echo.demo.api.EchoService;
import com.github.freshchen.echo.rpc.client.annotation.RpcReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@RestController
@Slf4j
public class EchoController {


    @RpcReference
    private EchoService echoService;


    @GetMapping("/echo")
    public Result echo() throws InterruptedException, ExecutionException {
//        NettyClientChannel connect = client.connect("127.0.0.1", 8888);
//
//        RpcPackage rpcPacket = RpcPackage.builder().data("echo213123123123".getBytes(StandardCharsets.UTF_8)).build();
//        Channel channel = connect.getChannel();
//        ChannelFuture channelFuture = channel.writeAndFlush(rpcPacket);
//        boolean result = channelFuture.await(5000, TimeUnit.MILLISECONDS);
        Object echo = echoService.echo("hello");
        return Result.of(echo.toString());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Result {
        private String data;
    }

}
