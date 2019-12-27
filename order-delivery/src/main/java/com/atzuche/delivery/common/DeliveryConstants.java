package com.atzuche.delivery.common;

/**
 * @author 胡春林
 * 交接车相关常量
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

    public static final String CANCEL_FLOW_ORDER = "http://114.55.63.205:8888/AOTU_TEST/" + "AotuInterface/cancelfloworder";

    public static final String CHANGE_FLOW_ORDER = "http://114.55.63.205:8888/AOTU_TEST/" + "AotuInterface/floworderchange";

    public static final String ADD_FLOW_ORDER = "http://114.55.63.205:8888/AOTU_TEST/" + "AotuInterface/addflowprocessdata";

    public static final String ACCESS_KEY = "b2dOPcGuP1DKGtj3IeAospt1nZQfJGlRm/sUl0KuZlsVnP1ieM+ag9aA3n0DJcXbnZ8VvHjlbcWp+UV+LQ+OUA==";


    public final static String REN_YUN_QUEUE_KEY = "trans-car-progress";

    public final static String CAR_ON_ROAD_WAITING = "车辆已检查完毕，正在途中";

    public final static String CTRIP = "ctrip";

    public final static String VENDOR_CODE = "13021";
    public final static String CTRIP_CODE = "Aotu&^.pL";

    public static final String CTRIP_STATUS_TRANS_FINISH = "com.autoyol.mns.queue.ctrip-status-trans-finish";


}
