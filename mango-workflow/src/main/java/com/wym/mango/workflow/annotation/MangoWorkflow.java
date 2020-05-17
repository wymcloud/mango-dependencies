package com.wym.mango.workflow.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MangoWorkflow {

    /**
     * 绑定的 bean name，留空使用对应的方法名。
     *
     * @return
     */
    String name() default "";

    /**
     * handler 组，支持多重定义。
     *
     * @return
     */
    Qualifier[] handlers();

}
