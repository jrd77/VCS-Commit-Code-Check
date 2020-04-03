package com.atzuche.order.accountrenterwzdepost.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class AccountRenterWZDepositResVO {
    /**
     *
     */
    private Integer id;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 应收违章押金
     */
    private Integer yingshouDeposit;
    /**
     * 实收违章押金
     */
    private Integer shishouDeposit;
    /**
     * 是否预授权
     */
    private Integer isAuthorize;
    
    /**
     * 免押方式
     */
    private Integer freeDepositType;
    
    /**
     * 是否免押
     */
    private Integer isFreeDeposit;
    /**
     * 应退押金
     */
    private Integer shouldReturnDeposit;
    /**
     * 实退押金
     */
    private Integer realReturnDeposit;
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

    /**
     * 结算状态 0未结算 1 已结算
     * 枚举类型 SettleStatusEnum
     */
    private Integer settleStatus;
    
    /**
     * 预授权金额
     */
    private Integer authorizeDepositAmt;
    /**
     * 信用支付金额
     */
    private Integer creditPayAmt;
    /**
     * 剩余押金总额
     */
    private Integer surplusDepositAmt;
    /**
     * 剩余预授权金额
     */
    private Integer surplusAuthorizeDepositAmt;
    /**
     * 剩余信用支付金额
     */
    private Integer surplusCreditPayAmt;


}
