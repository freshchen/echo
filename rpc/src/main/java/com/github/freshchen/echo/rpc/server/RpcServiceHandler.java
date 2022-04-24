package com.github.freshchen.echo.rpc.server;

import com.github.freshchen.echo.rpc.common.util.Asserts;
import com.github.freshchen.echo.rpc.protocol.RpcProto;
import com.github.freshchen.echo.rpc.registry.Registry;
import com.github.freshchen.echo.rpc.registry.model.ReferenceInfo;
import com.github.freshchen.echo.rpc.server.annotation.RpcService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Slf4j
public class RpcServiceHandler implements BeanPostProcessor, BeanFactoryAware, ApplicationRunner, PriorityOrdered {

    private static final Set<ReferenceInfo> UNREGISTER_SERVICES_SET = ConcurrentHashMap.newKeySet();
    private static final Set<ReferenceInfo> REGISTER_SERVICES = ConcurrentHashMap.newKeySet();
    private BeanFactory beanFactory;
    private Registry register;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> clz = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        RpcService rpcService = clz.getAnnotation(RpcService.class);
        if (rpcService != null) {
            ReferenceInfo serviceInfo = ReferenceInfo.of(rpcService.id(), rpcService.applicationName(), clz);
            UNREGISTER_SERVICES_SET.add(serviceInfo);
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, List<ReferenceInfo>> idServiceMap = UNREGISTER_SERVICES_SET.stream()
                .collect(Collectors.groupingBy(ReferenceInfo::getServiceName));
        idServiceMap.forEach((k, v) -> {
            Asserts.notBlank(k, "service id is blank " + v);
            Asserts.isTrue(v.size() == 1, "service id repeated " + v);
        });
        register = beanFactory.getBean(Registry.class);
        Map<ReferenceInfo, Boolean> registerResult = this.register.register(UNREGISTER_SERVICES_SET);
        Asserts.isTrue(registerResult.size() == UNREGISTER_SERVICES_SET.size(), "register result size error");
        registerResult.forEach((k, v) -> {
            Asserts.isTrue(v, "service register failed " + k);
            REGISTER_SERVICES.add(k);
        });
        log.info("service register total: {}", REGISTER_SERVICES.size());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public void handleRequest(ChannelHandlerContext ctx, RpcProto.Package msg) {

        ctx.channel().writeAndFlush(msg);
    }
}
