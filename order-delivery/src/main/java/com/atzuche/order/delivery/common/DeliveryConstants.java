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

    //pro
    public static final String CANCEL_FLOW_ORDER = "http://114.55.179.151:8888/HRM3_WEB_AOTU/" + "AotuInterface/cancelfloworder";
    public static final String CHANGE_FLOW_ORDER = "http://114.55.179.151:8888/HRM3_WEB_AOTU/" + "AotuInterface/floworderchange";
    public static final String ADD_FLOW_ORDER = "http://114.55.179.151:8888/HRM3_WEB_AOTU/" + "AotuInterface/addflowprocessdata";

    //test
    public static final String CANCEL_FLOW_ORDER_TEST2 = "http://114.55.63.205:8878/AOTU_TEST02/" + "AotuInterface/cancelfloworder";
    public static final String CHANGE_FLOW_ORDER_TEST2 = "http://114.55.63.205:8878/AOTU_TEST02/" + "AotuInterface/floworderchange";
    public static final String ADD_FLOW_ORDER_TEST2 = "http://114.55.63.205:8878/AOTU_TEST02/" + "AotuInterface/addflowprocessdata";

    public static final String ACCESS_KEY = "b2dOPcGuP1DKGtj3IeAospt1nZQfJGlRm/sUl0KuZlsVnP1ieM+ag9aA3n0DJcXbnZ8VvHjlbcWp+UV+LQ+OUA==";


    public final static String REN_YUN_QUEUE_KEY = "trans-car-progress";

    public final static String CAR_ON_ROAD_WAITING = "车辆已检查完毕，正在途中";

    public final static String CTRIP = "ctrip";

    public final static String VENDOR_CODE = "13021";
    public final static String CTRIP_CODE = "Aotu&^.pL";

    public static final String CTRIP_STATUS_TRANS_FINISH = "com.autoyol.mns.queue.ctrip-status-trans-finish";

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
