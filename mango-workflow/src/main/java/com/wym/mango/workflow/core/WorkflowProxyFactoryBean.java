package com.wym.mango.workflow.core;


import com.wym.mango.workflow.ApiWorkflowInterface;
import com.wym.mango.workflow.annotation.MangoWorkflow;
import com.wym.mango.workflow.exception.ApiBaseException;
import com.wym.mango.workflow.exception.ApiInterruptException;
import com.wym.mango.workflow.model.ApiEntityWrapper;
import com.wym.mango.workflow.model.ApiRequest;
import com.wym.mango.workflow.model.ApiResponse;
import com.wym.mango.workflow.model.ApiResponseEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * WorkflowProxy 工厂类，用于动态代理生成 接口实现类。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-06
 **/
@Slf4j
public class WorkflowProxyFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware, BeanNameAware {

    /**
     * bean 缓存
     */
    private Map<String, ApiWorkflowInterface> workflowCachedMap = new HashMap<>();

    private final Class<T> workflowProxyInterface;

    private ApplicationContext applicationContext;
    @Getter
    private String beanName;


    /**
     * spring 会调用构造方法将 接口类型传入。
     *
     * @param workflowProxyInterface
     */
    private WorkflowProxyFactoryBean(Class<T> workflowProxyInterface) {
        this.workflowProxyInterface = workflowProxyInterface;
    }


    @Override
    public T getObject() {
        /**
         * 新建代理实例。
         */
        WorkflowProxyInstance instance = new WorkflowProxyInstance();
        /**
         * 这里直接使用 jdk 的动态代理实现，性能损耗较大，但是考虑到这里只在初始化的时候使用，忽略掉性能损耗。
         * 调优可以考虑使用 spring 的 代理实现。
         */
        return (T) Proxy.newProxyInstance(this.workflowProxyInterface.getClassLoader(), new Class[]{this.workflowProxyInterface}, instance);
    }

    @Override
    public Class<T> getObjectType() {
        return workflowProxyInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 从缓存池或者 application context 中查找 bean
     *
     * @param engineBeanName
     * @return
     */
    protected ApiWorkflowInterface getApiWorkflowEngineBean(String engineBeanName) {
        ApiWorkflowInterface apiWorkflow = workflowCachedMap.get(engineBeanName);
        if (apiWorkflow != null) {
            return apiWorkflow;
        }
        Object bean = applicationContext.getBean(engineBeanName);

        if (null == bean) {
            log.error("not found this bean '{}'. ", engineBeanName);
            throw new NoSuchBeanDefinitionException(engineBeanName);
        }

        if (!ApiWorkflowInterface.class.isAssignableFrom(bean.getClass())) {
            log.error("Bean '{}' type '{}' is wrong, need type '{}'.", engineBeanName, bean.getClass(), ApiWorkflowInterface.class);
            throw new TypeMismatchException(bean, ApiWorkflowInterface.class);
        }

        apiWorkflow = (ApiWorkflowInterface) bean;

        // 将 bean 缓存，提高性能。
        workflowCachedMap.put(engineBeanName, apiWorkflow);
        return apiWorkflow;
    }

    /**
     * WorkflowProxy 的代理实现类。
     * 这里使用内部类直接实现。
     */
    class WorkflowProxyInstance implements InvocationHandler, Serializable {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 如果是对象，直接返回。
            // 这里是基于接口扫描并注册的类，所以这里基本不可能。
            if (method.getDeclaringClass().equals(Object.class)) {
                return method.invoke(this, args);
            }

            // 记录日志。
            this.deprecatedLogging(method);

            /**
             * 获取代理方法的注解。
             */
            MangoWorkflow engine = method.getAnnotation(MangoWorkflow.class);
            // 没有注解抛出异常，也可以考虑做一个默认的实现。
            if (null == engine) {
//                log.error("You may be not use annotation '{}' for this method ''.", MangoWorkflow.class, method.getName());
                throw new UnsupportedOperationException(
                        String.format("class '%s' is an interface, you must use annotation '@%s' to implementation method '%s'.",
                                workflowProxyInterface.getName(), MangoWorkflow.class.getSimpleName(), method.getName())
                );
            }

            // 获取指定的 engine name
            String beanName = engine.name();

            // 如果没有设置名称，获取根据方法生成的默认 beanname
            if (!StringUtils.hasText(beanName)) {
                beanName = WorkflowBeanDefinitionProcessor.getWorkflowEnginerBeanName(method);
            }

            ApiWorkflowInterface apiWorkflow = getApiWorkflowEngineBean(beanName);

            ApiRequest request = null;
            Object[] others = null; // 保证不为 Null，方便业务逻辑判断
            if (!ObjectUtils.isEmpty(args)) {

                if (null == args[0]) { // 第一个个入参为空，报空指针。
                    throw new NullPointerException(
                            String.format("ApiWorkflowInterface's first argument must not be null, you can change the method '%s' without argument.", method.getName())
                    );
                }

                // 入参第一个参数不是 request，则将其使用 ApiEntityWrapper 进行包裹进行传递。
                if (!(ApiRequest.class.isAssignableFrom(args[0].getClass()))) {
                    request = ApiEntityWrapper.build(args[0]);
                } else {
                    request = (ApiRequest) args[0];
                }

                others = Arrays.copyOfRange(args, 1, args.length);
            }

            if (others == null) {
                others = new Object[]{};
            }

            ApiResponseEntity entity = new ApiResponseEntity();

            ApiResponse resp = null;





            Long timestamp = entity.getTimestamp();
            try {
                resp = apiWorkflow.execute(request, timestamp, others);
                /**
                 * @fix 这里理应不做异常处理，现阶段为了适应前端代码，做了统一的处理，从而使 response 统一返回 200。
                 */
            } catch (ApiBaseException e) {

                entity.setCode(e.getCode());
                entity.setMsg(e.getLocalizedMessage());
                if (e instanceof ApiInterruptException) {
                    resp = ((ApiInterruptException) e).getResponse();
                }
            } finally {
                apiWorkflow.after(request, resp, timestamp, others);

            }

            if (resp instanceof ApiEntityWrapper) { // 如果类型为透传，则直接提升级别。
                entity.setData(((ApiEntityWrapper) resp).getEntity());
            } else {
                entity.setData(resp);
            }

            return entity;
        }

        /**
         * 记录 deprecated 日志
         *
         * @param method
         */
        private void deprecatedLogging(Method method) {
            Deprecated methodDeprecated = method.getAnnotation(Deprecated.class);
            Deprecated classDeprecated = workflowProxyInterface.getAnnotation(Deprecated.class);
            if (methodDeprecated == null && classDeprecated == null) {
                return;
            }
        }
    }
}
