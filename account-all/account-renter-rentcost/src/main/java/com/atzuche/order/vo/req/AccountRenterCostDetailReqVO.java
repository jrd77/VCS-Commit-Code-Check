package com.atzuche.order.vo.req;

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
    private Integer orderNo;
    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 支付来源code
     */
    private Integer paySourceCode;
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
    private Integer payChannelCode;
    /**
     * 支付渠道
     */
    private String payChannel;
    /**
     * 入账金额
     */
    private Integer amt;
    /**
     * 入账来源编码
     */
    private Integer sourceCode;
    /**
     * 入账来源编码描述
     */
    private String sourceDetail;
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
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaySourceCode(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
