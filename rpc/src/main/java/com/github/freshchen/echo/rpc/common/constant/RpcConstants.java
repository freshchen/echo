package com.github.freshchen.echo.rpc.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcConstants {

    public static final String COLON = ":";
    public static final int CPU_CORE = Runtime.getRuntime().availableProcessors();

}
