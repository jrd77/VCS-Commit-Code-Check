package com.atzuche.order.commons.vo.res.account;

import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class AccountRenterDepositResVO {
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
     * 支付状态
     */
    private Integer payStatus;
    /**
     * 支付时间
     */
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    /**
     * 结算状态
     */
    private Integer settleStatus;
    /**
     * 结算时间
     */
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
     * 免押方式(1:绑卡减免,2:芝麻减免,3:消费)
     */
    private Integer freeDepositType;
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
