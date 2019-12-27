package com.atzuche.order.settle.vo.req;

import lombok.Data;

@Data
public class SettleOrders {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;

}
