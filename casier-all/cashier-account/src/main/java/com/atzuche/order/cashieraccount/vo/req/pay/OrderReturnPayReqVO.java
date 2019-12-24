package com.atzuche.order.cashieraccount.vo.req.pay;

import lombok.Data;

/**
 * 支付系统退款请求请求签名参数
 */
@Data
public class OrderReturnPayReqVO extends OrderPayBaseReqVO {

    /**
     * 退款ID
     */
    private String refundId;
    /**
     * 支付标题
     */
    private int refundAmt;

    /**
     * 支付流水号
     */
    private String qn;

    /**
     * 补付第几笔
     */
    private String paySn;

    /**
     * 加密字段
     */
    private String payMd5;

}
