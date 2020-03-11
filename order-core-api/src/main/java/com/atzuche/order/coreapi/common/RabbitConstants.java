package com.atzuche.order.coreapi.common;

/**
 * rabbit 常量
 *
 *
 * @author pengcheng.fu
 * @date 2020/3/9 16:56
 */
public class RabbitConstants {

    /**
     * 车主同意订单处理租期重叠的其他订单 -- EXCHANGE
     */
    public static final String EXCHANGE_AUTO_ORDER_ACTION = "auto-order-action";
    /**
     * 车主同意订单处理租期重叠的其他订单 -- ROUTINGKEY
     */
    public static final String ROUTINGKEY_ORDER_AGREE_CONFLICT_NEW = "action.order.agree.conflict.notice.new";
    /**
     * 车主同意订单处理租期重叠的其他订单 -- QUEUE
     */
    public static final String QUEUE_ORDER_AGREE_CONFLICT_NEW = "order.agree.conflict.new.queue";


}
