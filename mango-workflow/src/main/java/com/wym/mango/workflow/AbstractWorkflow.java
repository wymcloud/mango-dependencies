package com.wym.mango.workflow;

import com.wym.mango.workflow.entity.WorkflowContext;

public interface AbstractWorkflow {

    /**
     * 处理程序
     */

    void handle(WorkflowContext context, Object... others) throws Exception;

}
