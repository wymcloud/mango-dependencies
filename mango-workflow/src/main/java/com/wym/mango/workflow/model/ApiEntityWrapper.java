package com.wym.mango.workflow.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用输入输出的 entity。
 * 使用场景：
 * 1. service 定义的第一个入参并非 ApiRequest 实现，使用 ApiEntityWrapper 进行包装，确保 handler 中可以调用。
 * 2. service 中定义的出参为 ApiEntityWrapper<T>，实际返回为 T。框架会对 wrapper 进行拆包。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-23
 **/
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEntityWrapper<T> implements ApiRequest, ApiResponse {

    private static final long serialVersionUID = 886506734868127764L;

    private final T entity;

    public static <O> ApiEntityWrapper<O> build(O entity) {
        return new ApiEntityWrapper<>(entity);
    }

}
