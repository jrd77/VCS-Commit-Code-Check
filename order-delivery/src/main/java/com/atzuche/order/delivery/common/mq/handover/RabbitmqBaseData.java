package com.atzuche.order.delivery.common.mq.handover;

public class RabbitmqBaseData {
    /**
     * rabbitmq的routeKey
     * 必填
     */
    private String routeKey;

    /**
     * 消息id，可使用uuid
     * 必填
     */
    private String messageId;

    /**
     * 时间戳，格式为yyyyMMddHHmmss
     * 必填
     */
    private String msgCreateTime;

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMsgCreateTime() {
        return msgCreateTime;
    }

    public void setMsgCreateTime(String msgCreateTime) {
        this.msgCreateTime = msgCreateTime;
    }

}
