package com.wym.mango.workflow;

import com.wym.mango.workflow.model.ApiContext;
import com.wym.mango.workflow.model.ApiRequest;
import com.wym.mango.workflow.model.ApiResponse;
import org.springframework.core.Ordered;

public interface ApiWorkflowHandler<REQ extends ApiRequest, RESP extends ApiResponse> extends Ordered {

    /**
     * 处理程序
     *
     * @param context 上下文
     * @return
     */
    void handle(ApiContext<REQ, RESP> context, Object... others) throws Exception;

    /**
     * 是否支持本 handler 处理。
     *
     * @return
     */
    boolean supports(ApiContext<REQ, RESP> context, Object... others);

}
