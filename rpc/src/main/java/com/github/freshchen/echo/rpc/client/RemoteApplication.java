package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.common.model.Endpoint;
import lombok.Getter;
import lombok.Setter;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Getter
@Setter
public class RemoteApplication extends Endpoint {

    private String applicationName;

    public RemoteApplication(String address) {
        super(address);
    }

    @Override
    public String toString() {
        return String.format("RemoteApplication:%s[%s:%d]", applicationName, getIp(), getPort());
    }

}
