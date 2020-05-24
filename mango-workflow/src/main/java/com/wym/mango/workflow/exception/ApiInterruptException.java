package com.wym.mango.workflow.exception;

import com.wym.mango.workflow.model.ApiResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 影响主业务流程的中断异常，遇到此异常，需要继续向上抛出。
 * 异常会将已经写入 response 的内容拿到一起 set。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-12
 **/
public class ApiInterruptException extends ApiBaseException {

    @Setter
    @Getter
    private ApiResponse response;

    public ApiInterruptException(String msg, int code) {
        super(msg, code);
    }

    public ApiInterruptException(String msg) {
        super(msg, 500);
    }
}
