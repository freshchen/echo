package com.github.freshchen.echo.rpc.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcConstants {

    public static final String TO_STRING = "toString";
    public static final Set<String> NOT_PROXY_METHOD_SET = new HashSet<>();

    static {
        NOT_PROXY_METHOD_SET.add("getClass");
        NOT_PROXY_METHOD_SET.add("hashCode");
        NOT_PROXY_METHOD_SET.add("equals");
        NOT_PROXY_METHOD_SET.add("clone");
        NOT_PROXY_METHOD_SET.add(TO_STRING);
        NOT_PROXY_METHOD_SET.add("notify");
        NOT_PROXY_METHOD_SET.add("notifyAll");
        NOT_PROXY_METHOD_SET.add("wait");
        NOT_PROXY_METHOD_SET.add("finalize");
    }


    public static final String COLON = ":";
    public static final int CPU_CORE = Runtime.getRuntime().availableProcessors();

}
