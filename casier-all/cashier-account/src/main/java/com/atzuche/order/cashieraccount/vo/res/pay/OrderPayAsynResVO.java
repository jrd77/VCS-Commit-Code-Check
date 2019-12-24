package com.atzuche.order.cashieraccount.vo.res.pay;

import lombok.Data;

/**
 * 支付系统回调 异步信息返回
 */
@Data
public class OrderPayAsynResVO extends OrderPayBaseResVO {


    private String settleAmount;

    private String orderTime;

    private String totalFreezeCreditAmount;

    private String totalFreezeFundAmount;

}
