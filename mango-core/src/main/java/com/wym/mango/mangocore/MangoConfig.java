package com.wym.mango.mangocore;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Data
public class MangoConfig {

    @Value("${mango.workflow.handle.path}")
    private String workflowPath;


}
