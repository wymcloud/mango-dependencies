package com.wym.mango.mangocore.workflow.handle;

import com.wym.mango.workflow.ApiWorkflowHandler;
import com.wym.mango.workflow.model.ApiContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("handle.test")
@Component
@Slf4j
public class handle1 implements ApiWorkflowHandler {


    @Override
    public void handle(ApiContext context, Object... others) throws Exception {
        log.info("-------------进入handle 1---------------------");
    }

    @Override
    public boolean supports(ApiContext context, Object... others) {
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
