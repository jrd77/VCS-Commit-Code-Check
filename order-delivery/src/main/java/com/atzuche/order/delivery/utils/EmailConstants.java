package com.atzuche.order.delivery.utils;

/**
 * @author yi.liu
 */
public class EmailConstants {

    /**
     * 仁云接口失败，提醒邮件
     */
    public static final String PROCESS_SYSTEM_NOTICE_SUBJECT = "取送车接口-异常预警";
    /**
     * 订单号，接口名称+链接，取送车类型（取车服务or还车服务），有问题，请及时查询！
     */
    public static final String PROCESS_SYSTEM_NOTICE_CONTENT = "[console]订单号:%s，接口名称:%s，链接: %s ，取送车类型（%s） ，有问题，请及时查询！";
}
