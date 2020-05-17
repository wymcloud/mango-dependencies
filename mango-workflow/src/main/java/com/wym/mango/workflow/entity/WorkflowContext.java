package com.wym.mango.workflow.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 上下文用于传递数据。
 *
 *
 **/
@Data
public class WorkflowContext implements Serializable {

    private static final long serialVersionUID = 4242698272712880039L;


    /**
     * 用于参数传递
     */

    private final Map<String, Object> parameters;


    public WorkflowContext(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
