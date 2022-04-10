package com.github.freshchen.echo.rpc.server.annotation;


import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface RpcService {

    String id() default "";

    String applicationName() default "";

}
