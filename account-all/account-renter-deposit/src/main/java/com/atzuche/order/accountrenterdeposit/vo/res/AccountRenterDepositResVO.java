package com.atzuche.order.accountrenterdeposit.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class AccountRenterDepositResVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 支付状态
     */
    private Integer payStatus;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 结算状态
     */
    private Integer settleStatus;
    /**
     * 结算时间
     */
    private LocalDateTime settleTime;
    /**
     * 应付押金总额
     */
    private Integer yingfuDepositAmt;
    /**
     * 实付押金总金额
     */
    private Integer shifuDepositAmt;
    /**
     * 预授权金额
     */
    private Integer authorizeDepositAmt;
    /**
     * 信用支付金额
     */
    private Integer creditPayAmt;
    /**
     * 剩余信用支付金额
     */
    private Integer surplusCreditPayAmt;
    /**
     * 剩余押金总额
     */
    private Integer surplusDepositAmt;
    /**
     * 剩余预授权金额
     */
    private Integer surplusAuthorizeDepositAmt;
    /**
     * 免押金额
     */
    private Integer reductionAmt;
    /**
     * 开启免押
     */
    private Integer isFreeDeposit;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     */
    private String createOp;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    private String updateOp;




}
