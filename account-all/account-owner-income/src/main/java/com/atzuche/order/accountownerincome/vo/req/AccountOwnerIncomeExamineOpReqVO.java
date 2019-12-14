package com.atzuche.order.accountownerincome.vo.req;

import com.atzuche.order.accountownerincome.enums.AccountOwnerIncomeExamineStatus;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 车主待审核收益请求信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerIncomeExamineOpReqVO {

    /**
     * 会员号
     */
    private Integer memNo;

    /**
     * 车主审核收益信息
     */
    private Integer accountOwnerIncomeExamineId;

    /**
     * 审核车主状态类型
     */
    private AccountOwnerIncomeExamineStatus status;

    /**
     * 收益审核描述
     */
    private String detail;

    /**
     * 审核人
     */
    private String opName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新人
     */
    private String updateOp;

    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getStatus(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAccountOwnerIncomeExamineId(), ErrorCode.PARAMETER_ERROR.getText());

    }

}
