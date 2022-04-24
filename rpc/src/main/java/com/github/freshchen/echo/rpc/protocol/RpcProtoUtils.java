package com.github.freshchen.echo.rpc.protocol;

import com.github.freshchen.echo.rpc.common.model.RpcException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcProtoUtils {
    public static RpcProto.DataSerializeType getSerializeType(Class<?> parameterType) {
        if (String.class.isAssignableFrom(parameterType)) {
            return RpcProto.DataSerializeType.STRING;
        }
        throw new RpcException("not supported type: " + parameterType);
    }
}
