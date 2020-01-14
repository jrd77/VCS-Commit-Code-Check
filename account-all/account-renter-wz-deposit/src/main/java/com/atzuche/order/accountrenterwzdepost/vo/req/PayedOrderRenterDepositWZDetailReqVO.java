package com.atzuche.order.accountrenterwzdepost.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 支付成功 回调 押金进出明细
 */
@Data
public class PayedOrderRenterDepositWZDetailReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     *
     */
    private String memNo;
    /**
     * 支付方式
     */
    private String payment;
    /**
     * 支付渠道
     */
    private String payChannel;
    /**
     * 费用编码
     */
    private Integer costCode;
    /**
     * 费用描述
     */
    private String costDetail;
    /**
     * 入账金额
     */
    private Integer amt;

    /**
     * 押金扣除来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 预授权金额
     */
    private Integer authorizeAmt;
    /**
     * 预授权到期时间
     */
    private LocalDateTime authorizeExpireTime;
    /**
     * 收银凭证
     */
    private String uniqueNo;

    /**
     * 创建人
     */
    private String createOp;

    /**
     *
     */
    private String updateOp;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
