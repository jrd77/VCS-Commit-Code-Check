package com.atzuche.order.delivery.common.mq.handover;

/**
 * @author 胡春林
 * 交接车事件信息
 */
public enum HandoverRabbitMQEventEnum {

    OWNER_TAKE("auto-order-handover", "handover.owner.take", "车主取车"),
    OWNER_BACK("auto-order-handover", "handover.owner.back", "车主还车"),
    RENTER_TAKE("auto-order-handover", "handover.renter.take", "租客取车"),
    RENTER_BACK("auto-order-handover", "handover.renter.back", "租客还车");

    public final String exchange;
    public final String routingKey;
    public final String name;

    HandoverRabbitMQEventEnum(String exchange, String routingKey, String name) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.name = name;
    }

}
