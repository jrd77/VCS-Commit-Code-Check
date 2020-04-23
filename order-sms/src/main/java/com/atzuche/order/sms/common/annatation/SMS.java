package com.atzuche.order.sms.common.annatation;


import java.lang.annotation.*;

/**
 * @author 胡春林
 * sms flag
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SMS {

    String renterFlag() default "";
    String ownerFlag() default "";
}
