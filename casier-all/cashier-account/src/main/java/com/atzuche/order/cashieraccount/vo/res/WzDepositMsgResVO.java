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

    /**
     * 剩余可用违章押金
     */
    private int wzDepositSurplusAmt;
    /**
     * 预计/实际 暂扣金额
     */
    private int detainAmt;

    /**\
     * 预计/实际 抵扣租车费用
     */
    private int detainCostAmt;

    /**
     * 结算时候抵扣历史欠款
     */
    private int debtAmt;
    /**
     * 实际已退
     */
    private int refundAmt;

    /**
     * 扣款状态
     */
    private String debtStatus;

    /**
     * 扣款时间
     */
    private String deductionTime;
}
