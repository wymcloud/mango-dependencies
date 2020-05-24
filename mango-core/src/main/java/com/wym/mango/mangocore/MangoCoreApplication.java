package com.wym.mango.mangocore;

import com.wym.mango.workflow.core.MangoWorkflowConfigurer;
import com.wym.mango.workflow.core.WorkflowBeanDefinitionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;


@SpringBootApplication
public class MangoCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangoCoreApplication.class, args);
    }

    @Resource
    private MangoConfig mangoConfig;

    @Bean
    public MangoConfig getMangoConfig(){
        return mangoConfig;
    }

    @Bean
    public MangoWorkflowConfigurer mangoWorkflowConfigurer(ApplicationContext applicationContext) {
        return new MangoWorkflowConfigurer(new WorkflowBeanDefinitionProcessor(applicationContext), "123");
    }


}
