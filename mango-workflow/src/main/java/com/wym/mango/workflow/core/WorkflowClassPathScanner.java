package com.wym.mango.workflow.core;

import com.wym.mango.workflow.annotation.MangoWorkflow;
import com.wym.mango.workflow.annotation.MangoWorkflowProxy;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

/**
 * 类扫描及后续处理的一个 scanner.
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-06
 **/
public class WorkflowClassPathScanner extends ClassPathBeanDefinitionScanner {

    private final WorkflowBeanDefinitionProcessor workflowBeanDefinitionProcessor;

    public WorkflowClassPathScanner(BeanDefinitionRegistry registry, WorkflowBeanDefinitionProcessor workflowBeanDefinitionProcessor) {
        super(registry, false);

        this.addIncludeFilter(new AnnotationTypeFilter(MangoWorkflowProxy.class));

        this.workflowBeanDefinitionProcessor = workflowBeanDefinitionProcessor;

        this.workflowBeanDefinitionProcessor.setRegistry(registry);
    }


    /**
     * 复写方法，扫描包，获取接口，进行代理实现。
     *
     * @param basePackages
     * @return
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        if (holders.isEmpty()) {
            logger.warn("No Jsf Provider Interface found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
            return holders;
        }
        // 自动代理接口实现
        workflowBeanDefinitionProcessor.autoProxyInterface(holders);
        return holders;
    }

    /**
     * 必须是接口。
     *
     * @param beanDefinition
     * @return
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.isIndependent();
    }
}
