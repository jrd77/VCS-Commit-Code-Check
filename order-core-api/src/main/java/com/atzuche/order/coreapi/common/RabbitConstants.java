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
    public static final String ROUTINGKEY_ORDER_AGREE_CONFLICT_NEW = "special.action.order.agree.conflict.notice.new";
    /**
     * 车主同意订单处理租期重叠的其他订单 -- QUEUE
     */
    public static final String QUEUE_ORDER_AGREE_CONFLICT_NEW = "special.order.agree.conflict.new.queue";



    /**
     * 发送二清通知--exchange
     */
    public final static String SEND_SECONDARY_NOTICE_EXCHANGE = "auto_send_secondary_notice";
    /**
     * 资料审核通知--route.key
     */
    public final static String SEND_IMPACT_DATA_AUDIT_NOTICE_ROUTEKEY = "auto.send.impactData.audit.notice";
    /**
     * 资料审核通知--queue
     */
    public final static String SEND_IMPACT_DATA_AUDIT_NOTICE_QUEUE = "auto.send.impactData.audit.notice.queue";
    /**
     * 提现结果通知--route.key
     */
    public final static String SEND_WITHDRAWALS_NOTICE_ROUTEKEY = "auto.send.withdrawals.notice";
    /**
     * 提现结果通知--queue
     */
    public final static String SEND_WITHDRAWALS_NOTICE_QUEUE = "auto.send.withdrawals.notice.queue";
    /**
     * 车主收益上账通知--route.key
     */
    public final static String SEND_INCOMEAMT_TOACCOUNT_NOTICE_ROUTEKEY = "auto.send.owner.income.toAccount.notice";
    /**
     * 车主收益上账通知--queue
     */
    public final static String SEND_INCOMEAMT_TOACCOUNT_NOTICE_QUEUE = "auto.send.owner.income.toAccount.notice.queue";


}
