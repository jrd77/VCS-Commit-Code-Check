package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 *   订单的状态表支付状态
 0,待支付 1,已支付
 **/
@Getter
public enum OrderPayStatusEnum {
    /**
     * 已支付
     **/
    PAYED(1,"已支付"),
    /**
     * 待支付
     **/
    PAYING(0,"待支付");


    private int status;
    private String desc;

    /**
     *  constructor
     * @param status status value
     * @param desc  status description
     */
    OrderPayStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

    /**
     * convert int value to OrderStatus
     * @param status int value
     * @return
     */
    public OrderPayStatusEnum from(int status){
        OrderPayStatusEnum[] statuses = values();
        for(OrderPayStatusEnum s:statuses){
            if(status==s.status){
                return s;
            }
        }
        throw new RuntimeException("the value of status :"+status+" not supported,please check");
    }


}
