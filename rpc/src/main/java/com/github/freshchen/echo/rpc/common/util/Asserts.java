package com.github.freshchen.echo.rpc.common.util;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Asserts {

    public static void notBlank(String value, String msg) {
        if (StringUtils.isBlank(value)) {
            throw new RpcException(msg);
        }
    }

    public static void isTrue(boolean value, String msg) {
        if (!value) {
            throw new RpcException(msg);
        }
    }
}
