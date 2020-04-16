package com.atzuche.order.settle.vo.req;

import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 抵扣欠款传参
 * @author haibao.yan
 */
@Data
public class AccountDeductDebtReqVO {
	/**
	 * 订单号,处理支付欠款
	 */
	private String orderNo;

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 抵扣金额
     */
    private int amt;

    /**
     * 真实抵扣金额
     */
    private int realAmt;

    /**
     * 来源编码描述
     */
    private String sourceCode;
    /**
     * 来源编码（收银台/非收银台）
     */
    private String sourceDetail;
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
     * 欠款抵扣 收款list
     */
    List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetails;

    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
    }
}