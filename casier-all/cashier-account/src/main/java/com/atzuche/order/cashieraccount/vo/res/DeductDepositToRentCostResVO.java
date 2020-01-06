package com.atzuche.order.cashieraccount.vo.res;

import lombok.Data;

/**
 * 抵扣欠款传参
 * @author haibao.yan
 */
@Data
public class DeductDepositToRentCostResVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 剩余抵扣金额
     */
    private Integer amt;

    /**
     * 租客 剩余 车辆押金
     */
    private int depositAmt;




}
