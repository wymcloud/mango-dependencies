package com.wym.mango.workflow.config.WorkflowRegisitrar;

import com.wym.mango.workflow.annotation.MangoWorkflowProxy;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class MyClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {


    public MyClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    protected void registerFilters() {
//        addIncludeFilter(new AnnotationTypeFilter(MangoWorkflowProxy.class));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {

        }
        return super.doScan(basePackages);
    }
}
