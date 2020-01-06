package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 *   订单的主状态
 *      待确认 1
 *      待支付 4
 *      待调度 （需要调度的订单才有该状态）8
 *      待取车 16
 *      待还车 32
 *      待结算 64
 *      待违章结算 128
 *      待理赔处理 256
 *      完成  (completed) 512
 *      结束 （closed) 0
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 2:19 下午
 **/
@Getter
public enum OrderStatusEnum {
    /**
     * 订单状态-待确认
     **/
    TO_CONFIRM(1,"待确认"),
    /**
     * 订单状态-待支付
     **/
    TO_PAY(4,"待支付"),
    /**
     * 订单状态-待调度
     **/
    TO_DISPATCH(8,"待调度"),
    /**
     * 订单状态-待取车
     **/
    TO_GET_CAR(16,"待取车"),
    /**
     * 订单状态-待还车
     **/
    TO_RETURN_CAR(32,"待还车"),
    /**
     * 订单状态-待结算
     **/
    TO_SETTLE(64,"待结算"),
    /**
     * 订单状态-待违章结算
     **/
    TO_WZ_SETTLE(128,"待违章结算"),
    /**
     * 订单状态-待理赔处理
     **/
    TO_CLAIM_SETTLE(256,"待理赔处理"),
    /**
     * 订单状态-完结
     **/
    COMPLETED(512,"完成"),
    /**
     * 订单状态-结束
     **/
    CLOSED(0,"结束");


    private int status;
    private String desc;

    /**
     *  constructor
     * @param status status value
     * @param desc  status description
     */
    OrderStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

    /**
     * convert int value to OrderStatus
     * @param status int value
     * @return
     */
    public OrderStatusEnum from(int status){
        OrderStatusEnum[] statuses = values();
        for(OrderStatusEnum s:statuses){
            if(status==s.status){
                return s;
            }
        }
        throw new RuntimeException("the value of status :"+status+" not supported,please check");
    }


}
