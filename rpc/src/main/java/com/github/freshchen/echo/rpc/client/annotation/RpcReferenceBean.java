package com.github.freshchen.echo.rpc.client.annotation;

import com.github.freshchen.echo.rpc.client.RpcReferenceProxy;
import com.github.freshchen.echo.rpc.common.util.ProxyFactory;
import com.github.freshchen.echo.rpc.registry.Registry;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Setter
@Getter
public class RpcReferenceBean implements FactoryBean, InitializingBean, BeanFactoryAware {

    private Class<?> interfaceClass;
    private String id;
    private String applicationName;
    private BeanFactory beanFactory;

    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() throws Exception {
        Registry registry = beanFactory.getBean(Registry.class);
        this.serviceProxy = ProxyFactory.createProxy(interfaceClass, RpcReferenceProxy.createRpcReferenceProxy(
                id, interfaceClass, applicationName, registry
        ));
    }

    @Override
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
