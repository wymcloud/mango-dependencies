package com.wym.mango.workflow.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 上下文用于传递数据。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-10
 **/
@ToString
public class ApiContext<REQ extends ApiRequest, RESP extends ApiResponse> implements Serializable {

    private static final long serialVersionUID = 4242698272712880039L;

    /**
     * 入参，只读
     */
    @Getter
    private final REQ request;

    /**
     * 出参，读写
     */
    @Getter
    @Setter
    private RESP response;

    /**
     * 来源，只读
     */
    @Getter
    private final String source;

    /**
     * 时间戳，只读
     */
    @Getter
    private final long timestamp;

    /**
     * 用于参数传递
     */
    private final Map<String, Object> parameters;

    public ApiContext(REQ request, String source, long timestamp) {
        this.request = request;
        this.source = source;
        this.timestamp = timestamp;
        this.parameters = new HashMap<>();
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, Object> setParameter(String key, Object value) {
        parameters.put(key, value);
        return parameters;
    }
    

}
