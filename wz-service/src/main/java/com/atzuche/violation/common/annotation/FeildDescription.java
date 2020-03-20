package com.atzuche.violation.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 胡春林
 * 字段注释
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FeildDescription {
    String value() default "";
}