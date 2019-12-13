package com.atzuche.order.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 抵扣欠款传参
 * @author haibao.yan
 */
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
     * 来源编码描述
     */
    private Integer sourceCode;
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
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()==0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
