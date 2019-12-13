package com.atzuche.order.vo.req;

import lombok.Data;

@Data
public class AccountDeductDebtReqVO {

    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 主订单号
     */
    private Integer orderNo;
    /**
     * 抵扣金额
     */
    private Integer amt;

    /**
     * 收款来源编码描述
     */
    private Integer sourceCode;
    /**
     * 收款来源编码（收银台/非收银台）
     */
    private String sourceDetail;
    /**
     * 收款凭证
     */
    private String uniqueNo;
    /**
     * 历史欠款id
     */
    private Integer debtDetailId;
    /**
     * 操作部门名称
     */
    private String deptName;
    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String createOp;
    /**
     * 更新人
     */
    private String updateOp;


}
