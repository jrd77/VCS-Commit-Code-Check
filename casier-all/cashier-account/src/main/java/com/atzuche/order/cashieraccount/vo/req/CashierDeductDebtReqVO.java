package com.atzuche.order.cashieraccount.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 抵扣欠款传参
 * @author haibao.yan
 */
@Data
public class CashierDeductDebtReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 结算剩余可抵扣金额
     */
    private Integer amt;

    /**
     * 租客子订单
     */
    private String renterOrderNo;

    /**
     * 押金扣除来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 收款凭证
     */
    private String uniqueNo;

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

    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
