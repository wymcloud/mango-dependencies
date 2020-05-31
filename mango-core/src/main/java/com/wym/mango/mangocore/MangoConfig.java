package com.wym.mango.mangocore;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ComponentScan(basePackages = {"com.wym.mango.mangocore.workflow"})
public class MangoConfig {

    @Value("${mango.workflow.handle.path}")
    private String workflowPath;

//    @Bean
//    public MangoWorkflowConfigurer mangoWorkflowConfigurer(ApplicationContext applicationContext) {
//        return new MangoWorkflowConfigurer(new WorkflowBeanDefinitionProcessor(applicationContext), workflowPath);
//    }

}
