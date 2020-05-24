package com.wym.mango.workflow.exception;

/**
 * 不影响主业务流程的异常，可以看做 warning 异常，需要在这里打印日志给出提示。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-12
 **/
public class ApiContinueException extends ApiBaseException {

    public ApiContinueException(String msg) {
        super(msg, 100);
    }
}
