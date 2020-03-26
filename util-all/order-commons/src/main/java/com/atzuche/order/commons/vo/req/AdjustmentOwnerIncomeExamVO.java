package com.atzuche.order.commons.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdjustmentOwnerIncomeExamVO {
    /*
    * 主键id
    * */
    @NotNull(message = "examineId不能为空 ")
    private Integer examineId;
    /*
    * 调账金额
    * */
    @NotNull(message = "调账金额不能为空")
    private Integer adjustmentAmt;
    /*
    * 审核状态 2,审核通过 3,审核拒绝
     * */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
    /*
    * 审核人
    * */
    private String auditOp;
}
