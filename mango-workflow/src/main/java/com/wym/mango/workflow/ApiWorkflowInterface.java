package com.wym.mango.workflow;

import com.wym.mango.workflow.exception.ApiBaseException;
import com.wym.mango.workflow.model.ApiRequest;
import com.wym.mango.workflow.model.ApiResponse;

/**
 * 接口 工作流，继承于 pipeline, 有自己独特的实现。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-13
 **/
public interface ApiWorkflowInterface<REQ extends ApiRequest, RESP extends ApiResponse> {

    /**
     * 简化方式，只传递 request 即可。
     *
     * @param request
     * @return
     */
    RESP execute(REQ request, long timestamp, Object... others) throws ApiBaseException;

    /**
     * 执行后的方法，主要用于事件上报
     * 如果行为过多的话，可以考虑抽取 Interceptor
     *
     * @param request
     * @param response
     * @param timestamp
     * @param others
     */
    void after(REQ request, RESP response, long timestamp, Object... others);
}
