package com.atzuche.order.accountownerincome.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主二清收益资金(冻结不可提现部分金额)进出明细
 * 
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:52
 */
@Data
public class AccountOwnerSecondaryIncomeInoutDetailEntity implements Serializable {


    private static final long serialVersionUID = -3791653172605375047L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 车主收益主键:account_owner_income.id
     */
    private Integer ownerIncomeId;

    /**
     * 当前车主收益(二清冻结不可提现金额)
     */
    private Integer incomeAmtBefore;

    /**
     * 进出资金(正值进 负值出)
     */
    private Integer inOutAmt;

    /**
     * 处理后车主收益(二清冻结不可提现金额)
     */
    private Integer incomeAmtAfter;

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
