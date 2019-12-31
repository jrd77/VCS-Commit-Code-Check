package com.atzuche.order.renterwz.aliyunmq.annotations;


import java.lang.annotation.*;

/**
 * This annation class define the message listener method for aliyun mns
 * Created by andy on 16/6/16.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AliyunMnsListener {
    String queueKey();
}
