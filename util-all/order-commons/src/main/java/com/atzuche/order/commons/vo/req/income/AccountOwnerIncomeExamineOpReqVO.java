package com.atzuche.order.commons.vo.req.income;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 车主待审核收益请求信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerIncomeExamineOpReqVO {
    /**
     * 会员号
     */
    private String memNo;

    /**
     * 主订单号
     */
    private String orderNo;


    /**
     * 车主审核收益信息
     */
    private Integer accountOwnerIncomeExamineId;

    /**
     * 审核车主状态类型
     */
    private Integer status;

    /**
     * 收益审核描述
     */
    private String detail;

    /**
     * 审核人
     */
    private String opName;

    /**
     * 更新人
     */
    private String updateOp;

    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getStatus(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAccountOwnerIncomeExamineId(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
