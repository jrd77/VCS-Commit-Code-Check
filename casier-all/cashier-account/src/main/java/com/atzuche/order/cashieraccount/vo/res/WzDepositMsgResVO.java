package com.atzuche.order.cashieraccount.vo.res;

import lombok.Data;

/**
 * 查询违章押金信息
 * @author haibao.yan
 */
@Data
public class WzDepositMsgResVO {

    /**
     * 会员号
     */
    private String memNo;

    /**
     *订单号
     */
    private String orderNo;

    /**
     *违章押金
     */
    private int wzDepositAmt;

    /**
     *应收
     */
    private int yingshouWzDepositAmt;

    /**
     *支付方式
     */
    private String paySource;

    /**
     *支付类型
     */
    private String payType;

    /**
     * 减免金额
     */
    private int reductionAmt;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 支付状态
     */
    private String payStatus;
}
