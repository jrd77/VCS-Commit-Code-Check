/**
 * 
 */
package com.atzuche.order.cashieraccount.vo.req.pay;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderPaySignBatchReqVO {
	/**
     * 选择的支付款项支付款项
     * 支付款项，01：租车押金，02：违章押金， 03补付租车押金
     支付款项，06：充值,07:支付欠款
     坦客支付款项，04:行程费用，05:押金费用
     来自配置DataPayKindConstant.class
     */
    @AutoDocProperty("支付款项")
    @NotNull(message = "payKind不能为空")
    private List<String> payKind         ;

    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    @NotNull(message = "menNo不能为空")
    private String menNo;
    /**
     * orderNo
     */
    @AutoDocProperty("订单号集合")
    @NotNull(message = "orderNos不能为空")
    private List<String> orderNos;

    /**
     * 是否使用钱包 0-否，1-是
     */
//    @AutoDocProperty("是否使用钱包 0-否，1-是")
//    private Integer isUseWallet = new Integer(0);  //默认否
    /**
     * 操作人
     */
    @AutoDocProperty("操作人")
    private Integer operator;

    /**
     * 操作人名称
     */
    @AutoDocProperty("操作人名称")
    private String operatorName;

    /**
     * 支付方式：transType "01"：消费，"02"：预授权， 消费方式："31"：消费撤销，"32"：预授权撤销，"03"：预授权完成，"04"：退货
     */
    @AutoDocProperty("支付方式：transType \"01\"：消费，\"02\"：预授权， 消费方式：\"31\"：消费撤销，\"32\"：预授权撤销，\"03\"：预授权完成，\"04\"：退货")
    @NotNull(message = "payType不能为空")
    private String payType = "01";  //默认消费

    /**
     * 微信公众号openid
     */
    @AutoDocProperty("微信公众号openid")
    private String openId;

    /**
     * 来源系统
     */
    @AutoDocProperty("来源系统(ANDROID ,IOS H5 ,WEB, MICROPROGRAM)")
    @NotNull(message = "reqOs不能为空")
    private String reqOs;

    /**
     * 支付来源
     */
    @AutoDocProperty("支付来源  01：手机银联\n" +
            "\t\t02.:新银联（含银联和applepay统一商户号）\n" +
            "\t\t06:支付宝支付，\n" +
            "\t\t07:微信支付(App), \n" +
            "\t\t08:快捷支付（快钱）\n" +
            "\t\t11.快捷支付（H5）     仅仅是source值不同。\n" +
            "\t\t12:Apple Pay\n" +
            "\t\t13. 微信支付(公众号)\n" +
            "\t\t14.连连支付\n" +
            "\t\t15. 微信支付(H5)")
    @NotNull(message = "paySource不能为空")
    private String paySource;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMenNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNos(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayType(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getReqOs(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPaySource(), ErrorCode.PARAMETER_ERROR.getText());

        Assert.isTrue(!CollectionUtils.isEmpty(getPayKind()), ErrorCode.PARAMETER_ERROR.getText());
    }
}