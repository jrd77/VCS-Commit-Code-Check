package com.atzuche.order.accountplatorm.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *  平台订单收益结算 信息返回
 * @author haibao.yan
 */
@Data
public class AccountPlatformProfitResVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主补贴金额
     */
    private Integer ownerSubsidyAmt;
    /**
     * 补贴车主金额
     */
    private Integer subsidyOwnerAmt;
    /**
     * 车主收益金额
     */
    private Integer ownerIncomeAmt;
    /**
     * 租客费用
     */
    private Integer renterCost;
    /**
     * 租客补贴金额
     */
    private Integer renterSubsidyAmt;
    /**
     * 补贴租客金额
     */
    private Integer subsidyRenterAmt;
    /**
     * 平台补贴
     */
    private Integer platformSubsidyAmt;
    /**
     * 平台应收费用金额
     */
    private Integer platformReceivableAmt;
    /**
     * 平台实收费用金额
     */
    private Integer platformReceivedAmt;
    /**
     * 状态（违章结算/订单结算）
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;


}
