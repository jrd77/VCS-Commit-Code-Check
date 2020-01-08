package com.atzuche.order.renterwz.dto;

/**
 * BaseMessageBody
 *
 * @author shisong
 * @date 2020/1/8
 */
public class BaseMessageBody {

    /**
     * 会员号
     */
    protected String memNo;

    /**
     * 消息唯一ID
     */
    protected String messageId;

    /**
     * 系统消息类型
     */
    protected String messageType;

    /**
     * 数据库配置字段
     */
    protected String event;

    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "BaseMessageBody{" +
                "memNo='" + memNo + '\'' +
                ", messageId='" + messageId + '\'' +
                ", messageType='" + messageType + '\'' +
                ", event='" + event + '\'' +
                '}';
    }
}
