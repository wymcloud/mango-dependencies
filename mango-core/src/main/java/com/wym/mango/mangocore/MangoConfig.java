package com.wym.mango.mangocore;


import com.wym.mango.workflow.config.EnableMangoWorkflow;
import com.wym.mango.workflow.config.MangoWorkflowConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Import(MangoWorkflowConfig.class)
//@EnableMangoWorkflow("com.wym.mango.mangocore.workflow.service")
public class MangoConfig {



}
