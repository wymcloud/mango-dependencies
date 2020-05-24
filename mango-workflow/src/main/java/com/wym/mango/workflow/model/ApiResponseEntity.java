package com.wym.mango.workflow.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 公共响应实体类。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-14
 **/
@Data
public class ApiResponseEntity<T> implements Serializable {

    private static final long serialVersionUID = 2219053580083877694L;

    /**
     * 响应代码，基于 HttpStatusCode 的一个扩展。
     * HttpStatus.OK.value() = 200
     */
    private Integer code = 200;
    /**
     * 响应消息，一般在有错误的情况下有内容。
     * HttpStatus.OK.getReasonPhrase() = OK
     */
    private String msg = "OK";
    /**
     * 响应内容。
     */
    private T data;
    /**
     * 请求开始时间戳
     */
//    private long requestTimestamp;
    /**
     * 请求结束时间戳
     */
    private Long timestamp = System.currentTimeMillis();

    public boolean isSuccess() {
        return code != null && code.intValue() == 200;
    }

}