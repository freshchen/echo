package com.github.freshchen.echo.rpc.client.annotation;

import cn.hutool.core.util.StrUtil;
import com.github.freshchen.echo.rpc.common.model.RpcException;
import com.github.freshchen.echo.rpc.common.util.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

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
public class RpcReferencePostProcessor implements SmartInstantiationAwareBeanPostProcessor,
        MergedBeanDefinitionPostProcessor,
        BeanFactoryAware,
        PriorityOrdered {

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(32);

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
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
        String cacheKey = (StringUtils.isNotBlank(beanName) ? beanName : clazz.getName());
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
                    currElements.add(new RpcReferenceFieldElement(field, rpcReference, beanFactory));
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

    private static class RpcReferenceMethodElement extends InjectionMetadata.InjectedElement {

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

    private static class RpcReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private final Annotation annotation;
        private final DefaultListableBeanFactory beanFactory;

        protected RpcReferenceFieldElement(Field member, Annotation annotation, DefaultListableBeanFactory beanFactory) {
            super(member, null);
            this.annotation = annotation;
            this.beanFactory = beanFactory;
        }

        @Override
        protected void inject(Object bean, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            try {
                ReflectionUtils.makeAccessible(field);
                Object value = field.get(bean);
                if (annotation instanceof RpcReference) {
                    Class<?> type = field.getType();
                    Asserts.isTrue(type.isInterface(), type + " is not a interface");
                    RpcReference rpcReference = (RpcReference) this.annotation;
                    String beanName = getBeanName(rpcReference, type);
                    try {
                        value = beanFactory.getBean(beanName);
                    } catch (NoSuchBeanDefinitionException ex) {
                        // continue the following logic to create new factory bean
                    }
                    if (value == null) {
                        value = createRpcReferenceBean(rpcReference, type, beanFactory);
                    }
                }
                if (value != null) {
                    ReflectionUtils.makeAccessible(field);
                    field.set(bean, value);
                }
            } catch (Throwable ex) {
                throw new RpcException("Could not inject field: " + field, ex);
            }
        }
    }

    private static String getBeanName(RpcReference rpcReference, Class<?> type) {
        String applicationName = rpcReference.applicationName();
        String serviceName = rpcReference.serviceName();
        if (StringUtils.isNoneBlank(applicationName, serviceName)) {
            return applicationName + StrUtil.upperFirst(serviceName);
        }
        Asserts.isTrue(type.isInterface(), type + " is not a interface");
        return type.getName();
    }

    private static Object createRpcReferenceBean(RpcReference rpcReference,
                                                 Class<?> serviceInterface,
                                                 DefaultListableBeanFactory beanFactory) {
        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setBeanClass(RpcReferenceBean.class);
        String beanName = getBeanName(rpcReference, serviceInterface);
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("interfaceClass", serviceInterface);
        values.addPropertyValue("beanName", beanName);
        values.addPropertyValue("serviceName", rpcReference.serviceName());
        values.addPropertyValue("applicationName", rpcReference.applicationName());
        definition.setPropertyValues(values);
        beanFactory.registerBeanDefinition(beanName, definition);
        return beanFactory.getBean(beanName);
    }


}
