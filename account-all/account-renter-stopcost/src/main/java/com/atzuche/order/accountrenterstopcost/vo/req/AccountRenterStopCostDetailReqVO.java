package com.atzuche.order.accountrenterstopcost.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 停运费资金进出请求参数
 */
@Data
public class AccountRenterStopCostDetailReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 入账金额
     */
    private int amt;
    /**
     * 入账时间
     */
    private LocalDateTime time;
    /**
     * 停运费来源编码
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 入账凭证
     */
    private String uniqueNo;
    /**
     * 支付方式编码
     */
    private Integer paymentCode;
    /**
     * 支付方式
     */
    private String payment;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 创建人
     */
    private String createOp;
    /**
     * 备注
     */
    private String remark;

    /**
     * 更新操作人
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
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
