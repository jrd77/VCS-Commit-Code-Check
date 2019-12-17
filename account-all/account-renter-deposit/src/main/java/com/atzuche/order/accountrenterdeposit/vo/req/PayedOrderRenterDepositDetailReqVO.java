package com.atzuche.order.accountrenterdeposit.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 支付成功 回调 押金进出明细
 */
@Data
public class PayedOrderRenterDepositDetailReqVO {

    /**
     *
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 支付方式
     */
    private Integer payment;
    /**
     * 支付渠道
     */
    private Integer paymentChannel;
    /**
     * 金额  转移出 为负
     */
    private Integer amt;
    /**
     * 预授权金额
     */
    private Integer authorizeDepositAmt;
    /**
     * 预授权到期时间
     */
    private LocalDateTime authorizeExpireTime;
    /**
     * 信用支付金额
     */
    private Integer creditPayAmt;
    /**
     * 信用支付到期时间
     */
    private LocalDateTime creditPayExpireTime;
    /**
     * 押金来源编码
     */
    private Integer sourceCode;
    /**
     * 押金来源编码描述
     */
    private String sourceDetail;
    /**
     * 押金凭证
     */
    private String uniqueNo;
    /**
     * 参数校验
     */

    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayment(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaymentChannel(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceDetail(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
