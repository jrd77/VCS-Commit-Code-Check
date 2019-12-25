package com.atzuche.order.accountrenterrentcost.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 租客费用明细落库 请求参数
 * @author haibao.yan
 */
@Data
public class AccountRenterCostDetailReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 支付来源code
     */
    private String paySourceCode;
    /**
     * 支付来源
     */
    private String paySource;
    /**
     * 支付方式code
     */
    private Integer paymentCode;
    /**
     * 支付方式
     */
    private String payment;
    /**
     * 支付渠道code
     */
    private String payChannelCode;
    /**
     * 支付渠道
     */
    private String payChannel;
    /**
     * 入账金额
     */
    private Integer amt;
    /**
     * 支付来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 交易时间
     */
    private LocalDateTime transTime;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaymentCode(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayChannelCode(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaySource(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
