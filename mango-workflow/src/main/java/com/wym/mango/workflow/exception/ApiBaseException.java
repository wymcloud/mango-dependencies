package com.wym.mango.workflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * api 接口服务基础异常信息。
 * 上层捕获需要用到这个异常处理。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-12
 **/
@AllArgsConstructor
public class ApiBaseException extends Exception {

    @Getter
    private final int code;

    @Getter
    @Setter
    private long timestamp;

    public ApiBaseException(String msg) {
        this(msg, 500);
    }

    public ApiBaseException(String msg, int code) {
        this(msg, code, System.currentTimeMillis());
    }

    public ApiBaseException(String msg, int code, long timestamp) {
        super(msg);
        this.code = code;
        this.timestamp = timestamp;
    }
}
