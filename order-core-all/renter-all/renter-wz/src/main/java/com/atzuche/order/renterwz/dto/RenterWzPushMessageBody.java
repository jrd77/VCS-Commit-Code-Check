package com.atzuche.order.renterwz.dto;

import com.atzuche.order.renterwz.enums.MessageTypeEnum;

import java.util.UUID;

/**
 * RenterWzPushMessageBody
 *
 * @author shisong
 * @date 2020/1/8
 */
public class RenterWzPushMessageBody extends BaseMessageBody{

    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public RenterWzPushMessageBody() {
        super.messageId = UUID.randomUUID().toString().replaceAll("-", "");
        super.messageType = MessageTypeEnum.ORDER_MESSAGE.getMessageType();
    }
}
