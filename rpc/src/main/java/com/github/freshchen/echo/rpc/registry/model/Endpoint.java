package com.github.freshchen.echo.rpc.registry.model;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.Validate;

import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.COLON;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@SuperBuilder
@EqualsAndHashCode
public class Endpoint {

    private String host;
    private int port;

    public Endpoint(String address) {
        Validate.notEmpty(address);
        String[] splits = address.split(COLON);
        Asserts.isTrue(2 == splits.length, "address error");
        this.host = splits[0];
        this.port = Integer.parseInt(splits[1]);
    }

    @Override
    public String toString() {
        return String.format("EndPoint{%s:%d}", host, port);
    }

}
