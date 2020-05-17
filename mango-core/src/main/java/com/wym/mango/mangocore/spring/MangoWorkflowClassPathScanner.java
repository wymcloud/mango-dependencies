package com.wym.mango.mangocore.spring;

import com.wym.mango.workflow.annotation.MangoWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * 类扫描及后续处理的一个 scanner.
 *
 **/
@Slf4j
public class MangoWorkflowClassPathScanner extends ClassPathBeanDefinitionScanner {
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        if (holders.size() == 0) {
            logger.warn("没有检测包");
        }
        return super.doScan(basePackages);
    }

    public MangoWorkflowClassPathScanner(BeanDefinitionRegistry registry) {
        super(registry);
        this.addIncludeFilter(new AnnotationTypeFilter(MangoWorkflow.class));
    }


}
