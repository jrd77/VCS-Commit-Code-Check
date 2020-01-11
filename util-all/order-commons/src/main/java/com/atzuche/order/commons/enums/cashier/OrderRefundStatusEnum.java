package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

/**
 *   订单的状态表支付状态
 0,待退款 1,已退款
 **/
@Getter
public enum OrderRefundStatusEnum {
    /**
     * 已退款
     **/
    REFUNDED(1,"已退款"),
    /**
     * 待退款
     **/
    REFUNDING(0,"待退款");


    private int status;
    private String desc;

    /**
     *  constructor
     * @param status status value
     * @param desc  status description
     */
    OrderRefundStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

    /**
     * convert int value to OrderStatus
     * @param status int value
     * @return
     */
    public OrderRefundStatusEnum from(int status){
        OrderRefundStatusEnum[] statuses = values();
        for(OrderRefundStatusEnum s:statuses){
            if(status==s.status){
                return s;
            }
        }
        throw new RuntimeException("the value of status :"+status+" not supported,please check");
    }


}
