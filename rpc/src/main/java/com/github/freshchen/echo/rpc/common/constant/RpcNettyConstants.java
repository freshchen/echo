package com.github.freshchen.echo.rpc.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcNettyConstants {

    public static final short MAGIC = (short) 0x0;
    public static final short MAGIC_LENGTH = 2;
    public static final int HEADER_LENGTH = 5;

    public static final int DEFAULT_WORKER_THREAD_NUMBER = RpcConstants.CPU_CORE * 2;

    public static final int DEFAULT_SO_BACKLOG = 256;
}
