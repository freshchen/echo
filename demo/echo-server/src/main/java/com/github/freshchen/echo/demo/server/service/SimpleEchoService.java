package com.github.freshchen.echo.demo.server.service;

import com.github.freshchen.echo.demo.api.EchoService;
import com.github.freshchen.echo.rpc.server.annotation.RpcService;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@RpcService
public class SimpleEchoService implements EchoService {
    @Override
    public String echo(String msg) {
        return msg;
    }
}
