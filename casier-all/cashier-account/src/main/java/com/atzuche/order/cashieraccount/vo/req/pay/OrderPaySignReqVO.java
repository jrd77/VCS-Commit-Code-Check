package com.atzuche.order.cashieraccount.vo.req.pay;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 支付系统查询请求签名参数
 */
@Data
public class OrderPaySignReqVO {



    /**
     * 选择的支付款项支付款项
     * 支付款项，01：租车押金，02：违章押金， 03补付租车押金
     支付款项，06：充值,07:支付欠款
     坦客支付款项，04:行程费用，05:押金费用
     来自配置DataPayKindConstant.class
     */
    private List<String> payKind         ;

    /**
     * 会员号
     */
    private String menNo;
    /**
     * orderNo
     */
    private String orderNo;

    /**
     * 是否使用钱包 0-否，1-是
     */
    private Integer isUseWallet;
    /**
     * 操作人
     */
    private Integer operator;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 支付方式：transType "01"：消费，"02"：预授权， 消费方式："31"：消费撤销，"32"：预授权撤销，"03"：预授权完成，"04"：退货
     */
    private String payType;

    /**
     * 微信公众号openid
     */
    String openId;

    /**
     * 来源系统
     */
    String OS;
    /**
     * 支付环境
     */
    String payEnv;
    /**
     * 支付来源
     */
    String paySource;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMenNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayType(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOS(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaySource(), ErrorCode.PARAMETER_ERROR.getText());

        Assert.isTrue(!CollectionUtils.isEmpty(getPayKind()), ErrorCode.PARAMETER_ERROR.getText());
    }
}
