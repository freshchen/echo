package com.github.freshchen.echo.rpc.registry.model;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author darcy
 * @since 2022/04/16
 **/
@Builder
@Getter
public class ClientBootstrapConfig {
    private int sharedIoThreadNum;

    private String serviceName;
    private int serviceIoThreadNum;

    private String applicationName;
    private int applicationIoThreadNum;

    private long timestamp;

    public Type checkAndGetType() {
        Asserts.notNull(timestamp);
        Asserts.isTrue(timestamp > 0);
        if (StringUtils.isNotBlank(serviceName)) {
            Asserts.notNull(serviceIoThreadNum);
            Asserts.isTrue(serviceIoThreadNum > 0);
            return Type.SERVICE;
        }
        if (StringUtils.isNotBlank(applicationName)) {
            Asserts.notNull(applicationIoThreadNum);
            Asserts.isTrue(applicationIoThreadNum > 0);
            return Type.APPLICATION;
        }
        Asserts.notNull(sharedIoThreadNum);
        Asserts.isTrue(sharedIoThreadNum > 0);
        return Type.SHARED;
    }

    @AllArgsConstructor
    @Getter
    public enum Type {

        SHARED("client-event-loop-shared-"),
        SERVICE("client-event-loop-service-%s-"),
        APPLICATION("client-event-loop-application-%s-");

        private String threadNamePrefix;
    }

}
