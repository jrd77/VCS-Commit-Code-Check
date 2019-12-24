package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 订单状态
 * <p>主订单状态: 0,待确认 1,待调度 2,待支付 3,待交车 4,待还车 5,待结算 6,待违章处理 7,待违章结算 8,已完结 9,待理赔处理 20,已结束</p>
 *
 * @author pengcheng.fu
 * @date 2019/12/24 19:32
 */

@Getter
public enum OrderStatusEnum {
    /**订单状态-待确认**/
    ORDER_STATUS_AWAIT_AFFIRM(0,"待确认"),
    /**订单状态-待调度**/
    ORDER_STATUS_AWAIT_DISPATCH(1,"待调度"),
    /**订单状态-待支付**/
    ORDER_STATUS_AWAIT_PAY(2,"待支付"),
    /**订单状态-待交车**/
    ORDER_STATUS_AWAIT_DELIVERY(3,"待交车"),
    /**订单状态-待还车**/
    ORDER_STATUS_AWAIT_RETURN(4,"待还车"),
    /**订单状态-待结算**/
    ORDER_STATUS_AWAIT_FEE_SETTLE(5,"待结算"),
    /**订单状态-待违章处理**/
    ORDER_STATUS_AWAIT_WZ_HANDLE(6,"待违章处理"),
    /**订单状态-待违章结算**/
    ORDER_STATUS_AWAIT_WZ_SETTLE(7,"待违章结算"),
    /**订单状态-已完结**/
    ORDER_STATUS_FINISH(8,"已完结"),
    /**订单状态-待理赔处理**/
    ORDER_STATUS_AWAIT_CLAIMS_HANDLE(8,"待理赔处理"),
    /**订单状态-已结束**/
    ORDER_STATUS_TERMINATE(20,"已结束"),



    ;


    private int code;

    private String name;

    OrderStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }



}


