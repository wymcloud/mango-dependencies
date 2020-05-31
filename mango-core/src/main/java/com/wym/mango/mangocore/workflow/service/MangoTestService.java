package com.wym.mango.mangocore.workflow.service;

import com.wym.mango.mangocore.workflow.model.TestRequest;
import com.wym.mango.workflow.annotation.MangoWorkflow;
import com.wym.mango.workflow.annotation.MangoWorkflowProxy;
import com.wym.mango.workflow.model.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;

@MangoWorkflowProxy
public interface MangoTestService {


    @MangoWorkflow(handlers = @Qualifier("handle.test"))
    ApiResponse testHandle(TestRequest request);

}
