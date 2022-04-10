package com.github.freshchen.echo.rpc.client;

import com.github.freshchen.echo.rpc.client.annotation.RpcReference;
import com.github.freshchen.echo.rpc.common.model.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author darcy
 * @since 2022/04/10
 **/
@Slf4j
public class RpcReferenceHandler implements SmartInstantiationAwareBeanPostProcessor,
        MergedBeanDefinitionPostProcessor,
        BeanFactoryAware,
        PriorityOrdered {

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(32);

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findRpcReferenceMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findRpcReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RpcException("Injection of RpcReference dependencies failed. beanName:" + beanName, ex);
        }
        return pvs;
    }

    private InjectionMetadata findRpcReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildRpcReferenceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildRpcReferenceMetadata(Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                RpcReference rpcReference = AnnotationUtils.findAnnotation(field, RpcReference.class);
                if (rpcReference != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("RpcReference annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    currElements.add(new RpcReferenceFieldElement(field, rpcReference));
                }
            });

            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                RpcReference rpcReference = AnnotationUtils.findAnnotation(bridgedMethod, RpcReference.class);
                if (rpcReference != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("RpcReference annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterCount() == 0) {
                        if (log.isInfoEnabled()) {
                            log.info("RpcReference annotation should only be used on methods with parameters: " +
                                    method);
                        }
                    }
                    currElements.add(new RpcReferenceMethodElement(method, rpcReference));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private class RpcReferenceMethodElement extends InjectionMetadata.InjectedElement {

        private final Annotation annotation;

        protected RpcReferenceMethodElement(Method member, Annotation annotation) {
            super(member, null);
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            super.inject(target, requestingBeanName, pvs);
        }

    }

    private class RpcReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private final Annotation annotation;

        protected RpcReferenceFieldElement(Field member, Annotation annotation) {
            super(member, null);
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            super.inject(target, requestingBeanName, pvs);
        }
    }

}
