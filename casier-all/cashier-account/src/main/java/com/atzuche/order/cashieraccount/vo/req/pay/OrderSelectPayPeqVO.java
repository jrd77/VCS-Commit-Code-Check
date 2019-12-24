package com.atzuche.order.cashieraccount.vo.req.pay;

import lombok.Data;

/**
 * 支付系统查询请求签名参数
 */
@Data
public class OrderSelectPayPeqVO extends OrderPayBasePeqVO{
    /**
     * paySn 补付第几笔
     */
    private String paySn;
    /**
     * payTime 支付时间
     */
    private String payTime;


}
