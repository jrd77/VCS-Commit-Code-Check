package com.atzuche.order.coreapi.enums;

/**
 * RabbitMqEnums
 *
 * @author shisong
 * @date 2019/12/28
 */
public enum  RabbitMqEnums {
    WZ_INFO("auto-order-center-wz","order.center.wz.info","order.center.wz.info.queue"),
    WZ_PRICE("auto-order-center-wz","order.center.wz.price","order.center.wz.price.queue"),
    WZ_VOUCHER("auto-order-center-wz","order.center.wz.voucher","order.center.wz.voucher.queue"),
    WZ_FEEDBACK("auto-order-center-wz","order.center.wz.feedback","order.center.wz.feedback.queue"),
    WZ_RESULT_FEEDBACK("auto-order-center-wz","order.center.wz.result.feedback","order.center.wz.result.feedback.queue"),
    WZ_CHE_LE_HANG_INFO("auto-order-center-wz","order.center.wz.che.le.hang.info","order.center.wz.che.le.hang.info.queue"),
    WZ_DE_RUN_CITIES("auto-order-center-wz","order.center.wz.cities","order.center.wz.cities.queue"),
    ORDER_PRERETURNCAR("auto-order-status","status.order.preReturnCar","status.order.preReturnCar.queue20200427"),
    ORDER_PRESETTLEMENT("auto-order-status","status.order.preSettlement","status.order.preSettlement.queue"),
    ORDER_END("auto-order-status","status.order.end","status.order.end.queue"),
    ORDER_WZ_SETTLEMENT_SUCCESS("auto-order-action","action.order.wz.settlement.success","action.order.wz.settlement.success.queue"),
    ORDER_WZ_SETTLEMENT_FAIL("auto-order-action","action.order.wz.settlement.fail","action.order.wz.settlement.fail.queue");

    private String exchange;

    private String routingKey;

    private String queueName;

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    RabbitMqEnums(String exchange, String routingkey, String queueName) {
        this.exchange = exchange;
        this.routingKey = routingkey;
        this.queueName = queueName;
    }
}
