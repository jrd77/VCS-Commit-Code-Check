package com.atzuche.order.sms.enums;

/**
 * @author ：weixu.chen
 * @date ：Created in 2019/7/17 16:27
 */
public enum MessageTypeEnum {

    ORDER_MESSAGE("ddxx", "订单消息"),
    PLATFORM_NOTICE("pttz", "平台通知"),
    SYSTEM_MESSAGE("xtxx", "系统消息"),
    ;

    /**
     * 消息类型,推送的消息类型
     */
    private String messageType;

    /**
     * 消息类型名称,推送的消息类型名称
     */
    private String messageTypeTitle;

    MessageTypeEnum(String messageType, String messageTypeTitle) {
        this.messageType = messageType;
        this.messageTypeTitle = messageTypeTitle;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageTypeTitle() {
        return messageTypeTitle;
    }

    @Override
    public String toString() {
        return "MessageTypeEnum{" +
                "messageType='" + messageType + '\'' +
                ", messageTypeTitle='" + messageTypeTitle + '\'' +
                '}';
    }
}
