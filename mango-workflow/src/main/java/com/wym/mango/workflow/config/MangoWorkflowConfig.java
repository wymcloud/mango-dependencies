package com.wym.mango.workflow.config;


import com.wym.mango.workflow.core.MangoWorkflowConfigurer;
import com.wym.mango.workflow.core.WorkflowBeanDefinitionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Configuration
@Component
public class MangoWorkflowConfig {

    private static final String path = "com.wym.mango.mangocore.workflow.service";

//    @PostConstruct
////    public void init(@Value("${mango.workflow.path}") String path){
////        System.out.println(path);
////
////    }
//    @Autowired
//    private Environment env;
//    @Resource
//    MangoConfigBean mangoConfigBean;

//    @Bean
    @Bean
    public MangoWorkflowConfigurer mangoWorkflowConfigurer(ApplicationContext applicationContext,Environment env) {
        return new MangoWorkflowConfigurer(new WorkflowBeanDefinitionProcessor(applicationContext), env.getProperty("asd"));
    }

}
