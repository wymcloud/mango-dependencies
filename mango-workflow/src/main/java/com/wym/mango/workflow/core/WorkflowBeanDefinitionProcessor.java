package com.wym.mango.workflow.core;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wym.mango.workflow.ApiWorkflowHandler;
import com.wym.mango.workflow.ApiWorkflowInterfaceEngine;
import com.wym.mango.workflow.annotation.MangoWorkflow;
import com.wym.mango.workflow.annotation.MangoWorkflowProxy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * jsf provider bean 处理器
 * 1. scan 期间修改 接口 类型的 bean，为其进行自动代理。
 * 2. scan 期间生成 jsf bean(支持属性注入)
 * 2. before init 期间，生成 workflow 的支持类。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-08
 **/
//@Slf4j
public class WorkflowBeanDefinitionProcessor {

    private Map<String, List<ApiWorkflowHandler>> cachedGroups = Maps.newLinkedHashMap();

    private final static String PREFIX_INTERFACE = "workflow.";
    private final static String PREFIX_JSF_PROVIDER_BEAN = "jsf.provider.";
    private final static String PREFIX_JSF_CONSUMER_BEAN = "jsf.consumer.";

    @Setter
    private BeanDefinitionRegistry registry;

    private final ApplicationContext applicationContext;

    /**
     * jsf server beans
     */
//    private final List<ServerBean> jsfServerBean;

    /**
     * 构造方法
     *
     * @param applicationContext
     */
    public WorkflowBeanDefinitionProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public WorkflowBeanDefinitionProcessor(ApplicationContext applicationContext, String jsfServerBeanName, ApplicationContext applicationContext1) {
        this.applicationContext = applicationContext1;
    }

    /**
     * 批量处理
     *
     * @param holders
     */
    public void autoProxyInterface(Collection<BeanDefinitionHolder> holders) {
        for (BeanDefinitionHolder holder : holders) {
            this.autoProxyInterface(holder);
        }
    }

    /**
     * 构建方法。
     * 算是一个入口。
     *
     * @param holder
     */
    public void autoProxyInterface(BeanDefinitionHolder holder) {
        GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
        String targetBeanClassName = definition.getBeanClassName();
        String beanName = holder.getBeanName();

        Class workflowProxyInterface = null;
        try {
            workflowProxyInterface = Class.forName(targetBeanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        MangoWorkflowProxy proxyAnnotation = (MangoWorkflowProxy) workflowProxyInterface.getAnnotation(MangoWorkflowProxy.class);
        if (proxyAnnotation == null) {
            return;
        }

        // 处理代理类
        this.buildWorkflowProxy(workflowProxyInterface, beanName, definition);

    }


    /**
     * 动态构造接口的 bean( spring 默认不支持接口初始化为 bean)。
     * 将扫描到的，注解的 handlers 都预先添加到代理 bean 中。
     *
     * @param definition
     */

    private void buildWorkflowProxy(Class targetClass, String beanName, GenericBeanDefinition definition) {

//        LOGGER.debug(
//                String.format("Creating WorkflowFactoryBean with name '%s' and '%s' workflowInterface",
//                        beanName, definition.getBeanClassName())
//        );

        /**
         * 放入两个构造参数
         */
        definition.getConstructorArgumentValues().addGenericArgumentValue(targetClass);

        definition.setBeanClass(WorkflowProxyFactoryBean.class);

        definition.applyDefaults(new BeanDefinitionDefaults());

        /**
         * 默认按照类型注入
         */
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

    }




    /**
     * 处理 workflow engine 的对外方法
     *
     * @param bean
     */
    public void processWorkflowEngines(Object bean) {
        // 判断是否为代理工厂类
        if (!WorkflowProxyFactoryBean.class.isAssignableFrom(bean.getClass())) {
            return;
        }

        Class workflowProxyInterface = ((WorkflowProxyFactoryBean) bean).getObjectType();
        Method[] methods = workflowProxyInterface.getDeclaredMethods();

        for (Method method : methods) {
            MangoWorkflow workflow = AnnotationUtils.findAnnotation(method, MangoWorkflow.class);

            if (workflow == null) {
                continue;
            }

            this.buildWorkflowEngine(workflow, method);
        }
    }

    /**
     * 构建 workflow engine
     *
     * @param workflow
     * @param method
     */
    private void buildWorkflowEngine(MangoWorkflow workflow, Method method) {
        String beanName = workflow.name();
        if (!StringUtils.hasText(beanName)) {
            beanName = getWorkflowEnginerBeanName(method);
        }

        // 如果这个 bean 已经注册
        if (this.existBean(beanName)) {
            return;
        }

        // 生成 bean definition
        BeanDefinition workflowBean = BeanDefinitionBuilder.genericBeanDefinition(ApiWorkflowInterfaceEngine.class)
                .addPropertyValue("handlers", this.getHandlers(workflow.handlers()))
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME)
                .getBeanDefinition();

        // 注册 bean definition
        this.registerBeanDefinition(beanName, workflowBean);
    }

    /**
     * 根据 qualifier 查找 handlers
     *
     * @param qualifiers
     * @return
     */
    private List<ApiWorkflowHandler> getHandlers(Qualifier[] qualifiers) {

        List<ApiWorkflowHandler> handlers = Lists.newArrayList();
        for (Qualifier qualifier : qualifiers) {
            handlers.addAll(getHandlers(qualifier.value()));
        }

        // 使用 spring 提供的 ordered 比较器进行比较排序。
        AnnotationAwareOrderComparator.sort(handlers);
        return handlers;
    }

    private List<ApiWorkflowHandler> getHandlers(String qualifier) {
        List<ApiWorkflowHandler> handlers = cachedGroups.get(qualifier);
        if (!CollectionUtils.isEmpty(handlers)) {
            return handlers;
        }
        handlers = Lists.newArrayList();

        Map<String, ApiWorkflowHandler> classmap = this.applicationContext.getBeansOfType(ApiWorkflowHandler.class);

        for (Map.Entry<String, ApiWorkflowHandler> kv : classmap.entrySet()) {
//                String handlerBeanName = kv.getKey();
            ApiWorkflowHandler handler = kv.getValue();

            Qualifier group = handler.getClass().getAnnotation(Qualifier.class);
            if (null == group || !group.value().equals(qualifier)) {
                continue;
            }
            // 如果有 doGroup，
            handlers.add(handler);
        }

        // 放入缓存。
        cachedGroups.put(qualifier, handlers);
        return handlers;
    }


    /**
     * 判断 bean 是否存在
     * TODO 这个判断可能不严谨，最好使用 spring 提供的严谨的判断方法。
     *
     * @param beanName
     * @return
     */
    private boolean existBean(String beanName) {
        return this.applicationContext.containsBean(beanName);
    }


    /**
     * 注册 bean definition
     *
     * @param name
     * @param beanDefinition
     */
    private void registerBeanDefinition(String name, BeanDefinition beanDefinition) {

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }


    /**
     * 获取方法名
     *
     * @param baseBeanName
     * @return
     */
    public static String getJsfProviderBeanName(String baseBeanName) {
        return PREFIX_JSF_PROVIDER_BEAN.concat(baseBeanName);
    }

    /**
     * 获取jsf consumer bean name
     *
     * @param baseBeanName
     * @return
     */
    public static String getJsfConsumerBeanName(String baseBeanName) {
        return PREFIX_JSF_CONSUMER_BEAN.concat(baseBeanName);
    }

    /**
     * 获取方法名
     *
     * @param method
     * @return
     */
    public static String getWorkflowEnginerBeanName(Method method) {
        return PREFIX_INTERFACE.concat(method.getName());
    }
}
