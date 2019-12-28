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
     * 是否使用钱包支付
     */
    private String isUseWallet;
    /**
     * 操作人
     */
    private Integer operator;

    /**
     * 操作人名称
     */
    private String operatorName;


    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMenNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(!CollectionUtils.isEmpty(payKind), ErrorCode.PARAMETER_ERROR.getText());
    }
}
