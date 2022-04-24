package com.github.freshchen.echo.rpc.common.util;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Asserts {

    public static void notNull(Object value) {
        if (Objects.isNull(value)) {
            throw new RpcException("value is null");
        }
    }

    public static void notBlank(String value) {
        if (StringUtils.isBlank(value)) {
            throw new RpcException("value is blank");
        }
    }

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

    public static void isTrue(boolean value) {
        if (!value) {
            throw new RpcException("not expected");
        }
    }

}
