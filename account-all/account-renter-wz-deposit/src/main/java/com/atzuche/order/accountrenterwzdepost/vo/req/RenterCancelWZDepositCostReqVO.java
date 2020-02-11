package com.atzuche.order.accountrenterwzdepost.vo.req;

import lombok.Data;

/**
 * 违章费用资金变动扣除
 */
@Data
public class RenterCancelWZDepositCostReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主子订单
     */
    private String renterOrderNo;
    /**
     * 会员号
     */
    private String memNo;

    /**
     * 金额
     */
    private int amt;

}
