package com.wym.mango.workflow.config;


import com.wym.mango.workflow.core.MangoWorkflowConfigurer;
import com.wym.mango.workflow.core.WorkflowBeanDefinitionProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 工作流配置类
 *
 */

@Configuration
@Component
public class MangoWorkflowConfig {

    @Bean
    public MangoWorkflowConfigurer mangoWorkflowConfigurer(ApplicationContext applicationContext, Environment env) {
        return new MangoWorkflowConfigurer(new WorkflowBeanDefinitionProcessor(applicationContext), env.getProperty("mango.workflow.path"));
    }

}
