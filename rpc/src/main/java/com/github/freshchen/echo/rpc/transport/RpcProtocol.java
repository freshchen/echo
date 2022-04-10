package com.github.freshchen.echo.rpc.transport;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@Builder
@ToString
@Getter
public class RpcProtocol {

    private byte[] data;

}
