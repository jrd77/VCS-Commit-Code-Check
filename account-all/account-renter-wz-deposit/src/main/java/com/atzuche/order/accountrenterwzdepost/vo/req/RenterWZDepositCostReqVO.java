package com.atzuche.order.accountrenterwzdepost.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 违章费用资金变动扣除
 */
@Data
public class RenterWZDepositCostReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主子订单
     */
    private String renterOrderNo;
    /**
     * 会员号
     */
    private String memNo;

    /**
     * 金额
     */
    private int amt;
    /**
     * 入账来源编码
     */
    private RenterCashCodeEnum renterCashCodeEnum;

    /**
     * 交易凭证
     */
    private String uniqueNo;
    /**
     * 部门
     */
    private String deptName;
    /**
     * 操作人
     */
    private String deptOp;
    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 更新人
     */
    private String updateOp;


    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
