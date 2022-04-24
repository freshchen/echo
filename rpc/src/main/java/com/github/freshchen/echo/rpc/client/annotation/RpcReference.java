package com.github.freshchen.echo.rpc.client.annotation;


import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcReference {

    String serviceName() default "";

    String applicationName() default "";

}
