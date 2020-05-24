package com.wym.mango.workflow.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**

 * 1. postProcessBeanDefinitionRegistry
 * 2. postProcessBeanFactory
 *    在这之前的 bean definition 可以查找到 ${} 类型的注解。 spring 会对注入的数据进行处理。
 *    在这之后定义的 bean definition 是找不到的，类已经完成了依赖分析。
 * 3. postProcessBeforeInitialization
 * 4. postProcessAfterInitialization
 *
 **/
public class MangoWorkflowConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanPostProcessor {


    /**
     * 需要扫描的包。
     */
    private String[] basePackages;

    private ApplicationContext applicationContext;

    private final WorkflowBeanDefinitionProcessor workflowBeanDefinitionProcessor;


    public MangoWorkflowConfigurer(WorkflowBeanDefinitionProcessor workflowBeanDefinitionProcessor, String... basePackages) {
        Assert.notEmpty(basePackages, "必须注入使用包名！");
        this.basePackages = basePackages;
        this.workflowBeanDefinitionProcessor = workflowBeanDefinitionProcessor;
    }




    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(basePackages, "必须注入使用包名！");
    }

    /**
     * 处理修改接口自动实现，自动生成 jsf bean.
     *
     * @param beanDefinitionRegistry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        WorkflowClassPathScanner scanner = new WorkflowClassPathScanner(beanDefinitionRegistry, this.workflowBeanDefinitionProcessor);

        scanner.setResourceLoader(this.applicationContext);
        scanner.scan(basePackages);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        保持空

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 在初始化前，需要处理 workflow engine 注解的相关方法。
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        /**
         * 方法内做判断。
         */
        this.workflowBeanDefinitionProcessor.processWorkflowEngines(bean);

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
