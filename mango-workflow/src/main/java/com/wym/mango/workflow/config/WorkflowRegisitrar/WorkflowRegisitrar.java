package com.wym.mango.workflow.config.WorkflowRegisitrar;

import com.wym.mango.workflow.annotation.MangoWorkflowProxy;
import com.wym.mango.workflow.config.EnableMangoWorkflow;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkflowRegisitrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware{


    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    @Bean
    public void asd(){
        System.out.println("---------------------------");
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        Assert.notEmpty(basePackages, "必须输入workflow包名！");
        String[] packages = basePackages.toArray(new String[basePackages.size()]);
//        ApplicationContext context = new AnnotationConfigApplicationContext(packages);
        MyClassPathBeanDefinitionScanner scanner = new MyClassPathBeanDefinitionScanner(registry, true);
        scanner.setResourceLoader(resourceLoader);
        scanner.registerFilters();
        Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(packages);
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            
        }
        //        new MangoWorkflowConfigurer(new WorkflowBeanDefinitionProcessor(applicationContext), packages);
//        MangoWorkflowConfigurer mangoWorkflowConfigurer = new MangoWorkflowConfigurer();
//        ClassPathBeanDefinitionScanner scanneraa = new ClassPathBeanDefinitionScanner
//        ClassPathScanningCandidateComponentProvider scanner = getScanner();
//        for (String basePackage : basePackages) {
//            Set<BeanDefinition> candidateComponents = scanner
//                    .findCandidateComponents(basePackage);
//            for (BeanDefinition candidateComponent : candidateComponents) {
//                if (candidateComponent instanceof AnnotatedBeanDefinition) {
//                    // verify annotated class is an interface
//                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
//                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
//                    Assert.isTrue(annotationMetadata.isInterface(),
//                            "@FeignClient can only be specified on an interface");
//                    autoProxyInterface(beanDefinition);
//                }
//            }
//        }
    }
    public void autoProxyInterface(AnnotatedBeanDefinition holder) {
        GenericBeanDefinition definition = (GenericBeanDefinition) holder.getOriginatingBeanDefinition();
        String targetBeanClassName = definition.getBeanClassName();
        String beanName = holder.getBeanClassName();

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
//        this.buildWorkflowProxy(workflowProxyInterface, beanName, definition);

    }
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMangoWorkflow.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        return basePackages;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {


        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
//                boolean isCandidate = false;
//                if (beanDefinition.getMetadata().isIndependent()) {
//                    if (!beanDefinition.getMetadata().isAnnotation()) {
//                        isCandidate = true;
//                    }
//                }
                return true;
            }
        };
    }


}
