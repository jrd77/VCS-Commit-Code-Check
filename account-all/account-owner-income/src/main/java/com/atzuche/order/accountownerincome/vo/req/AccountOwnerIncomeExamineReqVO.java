package com.atzuche.order.accountownerincome.vo.req;

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
     * 收益审核金额
     */
    private Integer amt;
    /**
     * 更新人
     */
    private String createOp;
    /**
     * 备注
     */
    private String remark;


    public void check() {
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
