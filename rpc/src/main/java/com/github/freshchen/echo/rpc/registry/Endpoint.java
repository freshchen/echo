package com.github.freshchen.echo.rpc.registry;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.github.freshchen.echo.rpc.common.constant.RpcConstants.COLON;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@Builder
public class Endpoint {

    private String ip;

    private int port;

    public Endpoint(String address) {
        Validate.notEmpty(address);
        String[] splits = address.split(COLON);
        Asserts.isTrue(2 == splits.length, "address error");
        this.ip = splits[0];
        this.port = Integer.parseInt(splits[1]);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(ip)
                .append(port)
                .toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        boolean flag = false;
        if (object != null && Endpoint.class.isAssignableFrom(object.getClass())) {
            Endpoint rhs = (Endpoint) object;
            flag = new EqualsBuilder()
                    .append(ip, rhs.ip)
                    .append(port, rhs.port)
                    .isEquals();
        }
        return flag;
    }

    @Override
    public String toString() {
        return String.format("EndPoint{%s:%d}", ip, port);
    }

}
