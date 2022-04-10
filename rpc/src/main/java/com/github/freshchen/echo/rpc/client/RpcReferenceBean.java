package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.transport.Client;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author darcy
 * @since 2022/04/10
 **/
public class RpcReferenceBean<T> implements FactoryBean<T>, InitializingBean, BeanFactoryAware {

    private Class<?> interfaceClass;
    private Client client;

    @Override
    public void afterPropertiesSet() throws Exception {
//        if (client == null) {
//            rpcClient = new RpcClient(namingServiceUrl, this, interceptors);
//        }
//        NamingOptions namingOptions = new NamingOptions();
//        namingOptions.setGroup(group);
//        namingOptions.setVersion(version);
//        namingOptions.setIgnoreFailOfNamingService(ignoreFailOfNamingService);
//        namingOptions.setServiceId(serviceId);
//        this.serviceProxy = BrpcProxy.getProxy(rpcClient, serviceInterface, namingOptions);
    }

    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
