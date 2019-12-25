package com.atzuche.order.cashieraccount.vo.req.pay;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 支付系统查询请求签名参数
 */
@Data
public class OrderPaySignReqVO {

    /**
     * atappId 凹凸APPID
     */
    private String atappId;

    /**
     *  支付ID
     */
    private String payId;

    /**
     *  支付项目
     */
    private String payKind;
    /**
     *  支付类型
     */
    private String payType         ;

    /**
     * 会员号
     */
    private String menNo;
    /**
     * orderNo
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private int payAmt;
    /**
     * 支付标题
     */
    private String payTitle;

    /**
     * 补付第几笔
     */
    private String paySn;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getAtappId(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMenNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayId(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayKind(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaySn(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayTitle(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayType(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
