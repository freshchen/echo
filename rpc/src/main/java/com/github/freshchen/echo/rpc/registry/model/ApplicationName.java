package com.github.freshchen.echo.rpc.registry.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author darcy
 * @since 2022/04/17
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationName implements EnvironmentAware {

    private static String name;

    @Override
    public void setEnvironment(Environment environment) {
        name = environment.getProperty("spring.application.name");
    }

    public static String get() {
        return name;
    }
}
