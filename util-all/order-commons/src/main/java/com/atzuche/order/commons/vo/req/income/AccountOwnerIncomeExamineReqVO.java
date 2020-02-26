package com.atzuche.order.commons.vo.req.income;

import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 车主待审核收益请求信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerIncomeExamineReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 收益审核金额
     */
    private int amt;

    /**
     * 审核状态1,待审核 2,审核通过 3,审核拒绝
     */
    private AccountOwnerIncomeExamineStatus status;

    /**
     * 类型，1收益，2调账
     */
    private AccountOwnerIncomeExamineType type;

    /**
     * 更新人
     */
    private String createOp;
    /**
     * 备注
     */
    private String remark;
    /**
     * 收益审核描述
     */
    private String detail;

    public void check() {
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOwnerOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getStatus(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getType(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
