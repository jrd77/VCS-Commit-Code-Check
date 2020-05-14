package com.atzuche.order.delivery.common;

/**
 * @author 胡春林
 * 交接车/配送相关常量
 */
public class DeliveryConstants {

    /**
     * 重试次数
     */
    public final static Integer REN_YUN_HTTP_RETRY_TIMES = 3;

    /**
     * 请求仁云的user-agent
     */
    public static final String USER_AGENT_OPERATECENTER = "Autoyol-OperateCenter";

    public static final String ACCESS_KEY = "b2dOPcGuP1DKGtj3IeAospt1nZQfJGlRm/sUl0KuZlsVnP1ieM+ag9aA3n0DJcXbnZ8VvHjlbcWp+UV+LQ+OUA==";

    public static final String SERVICE_TAKE_TEXT = "取车服务";
    public static final String SERVICE_BACK_TEXT = "还车服务";
    public static final String ADD_INTERFACE_NAME = "取送车派单流程生成订单服务单据";
    public static final String CHANGE_INTERFACE_NAME = "取送车派单流程订单服务单据信息变更";
    public static final String CANCEL_INTERFACE_NAME = "取送车派单流程取消订单服务单据";
    public static final String  EMAIL_PARAMS = "GetRevertNoticeEmails,取送车接口返回报错邮件提醒地址,a";
    public static final String ADD_TYPE = "add";
    public static final String CHANGE_TYPE = "update";
    public static final String CANCEL_TYPE = "cancel";

    /**CAT 记录请求响应时间**/
    public static final String REQUEST_REN_YUN_TYPE_PREFIX="REN_RUN:ADD:ORDER";


}
