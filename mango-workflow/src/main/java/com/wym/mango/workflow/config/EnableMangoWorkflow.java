package com.wym.mango.workflow.config;

import com.wym.mango.workflow.config.WorkflowRegisitrar.WorkflowRegisitrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WorkflowRegisitrar.class)
public @interface EnableMangoWorkflow {

    String[] value() default {};
}
