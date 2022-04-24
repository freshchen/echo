package com.github.freshchen.echo.rpc.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freshchen.echo.rpc.client.model.RpcFuture;
import com.github.freshchen.echo.rpc.registry.model.ApplicationName;
import com.github.freshchen.echo.rpc.registry.model.MethodInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.concurrent.CountDownLatch;

/**
 * @author darcy
 * @since 2022/04/14
 **/
public class Protocol implements BeanFactoryAware {

    private BeanFactory beanFactory;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public RpcProto.Package createRequestPackage(long correlationId,
                                                 MethodInfo methodInfo,
                                                 Object[] args) {
        RpcProto.Package.Builder packageBuilder = RpcProto.Package.newBuilder();
        packageBuilder.setCorrelationId(correlationId);
        packageBuilder.setApplicationName(ApplicationName.get());
        packageBuilder.setReqMeta(RpcProto.ReqMeta.newBuilder()
                .setServiceName(methodInfo.getServiceName())
                .setMethodName(methodInfo.getMethodName()).build());
        Class<?>[] actualParameterTypes = methodInfo.getActualParameterTypes();
        RpcProto.DataSerializeType[] parameterSerializeTypes = methodInfo.getParameterSerializeTypes();
        for (int i = 0; i < actualParameterTypes.length; i++) {
            Object arg = args[i];
            RpcProto.DataSerializeType type = parameterSerializeTypes[i];
            if (RpcProto.DataSerializeType.STRING.equals(type)) {
                packageBuilder.addData(RpcProto.Data.newBuilder().setType(type).setStringData((String) arg).build());
            }
        }
        return packageBuilder.build();
    }

    public RpcFuture createRequestRpcFuture(long correlationId) {
        RpcFuture rpcFuture = new RpcFuture(correlationId);
        rpcFuture.setLatch(new CountDownLatch(1));
        return rpcFuture;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
