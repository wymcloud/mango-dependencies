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
     *  绑定的name
     *
     * @return
     */
    String name() default "";

    /**
     *  定义的多个handles
     *
     * @return
     */
    Qualifier[] handlers();

}
