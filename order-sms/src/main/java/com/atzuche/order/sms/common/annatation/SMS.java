package com.atzuche.order.sms.common.annatation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 胡春林
 * sms flag
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SMS {

    String renterFlag() default "";
    String ownerFlag() default "";
}
