package com.atzuche.order.cashieraccount.vo.req.pay;

import lombok.Data;

/**
 * 支付系统支付请求签名参数
 */
@Data
public class OrderPayReqVO extends OrderPayBaseReqVO {


    /**
     * 支付金额
     */
    private int payAmt;
    /**
     * 支付标题
     */
    private String payTitle;

    /**
     * openid
     */
    private String openId;

    /**
     * 补付第几笔
     */
    private String paySn;

    /**
     * 加密字段
     */
    private String payMd5;

}
