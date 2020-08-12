package com.atzuche.order.accountownerincome.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主收益提现金额拆分明细
 * 
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:53
 */
@Data
public class AccountOwnerIncomeWithdrawSplitDetailEntity implements Serializable {

    private static final long serialVersionUID = -2441034967297337519L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 会员号
     */
    private String memNo;

    /**
     * 提现金额
     */
    private Integer withdrawAmt;

    /**
     * 老交易提现金额(来源member.balance)
     */
    private Integer oldTransWithdrawAmt;

    /**
     * 新交易提现金额(`account_owner_income`.income_amt)
     */
    private Integer newTransWithdrawAmt;

    /**
     * 二清提现金额(`account_owner_income`.secondary_income_amt)
     */
    private Integer secondaryWithdrawAmt;

    /**
     * 提现记录ids
     */
    private String cashExamineIds;

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
     * 修改人
     */
    private String updateOp;

    /**
     * 0-正常，1-已逻辑删除
     */
    private Integer isDelete;

}
