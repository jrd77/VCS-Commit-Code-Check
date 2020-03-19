package com.atzuche.order.cashieraccount.vo.req.pay;

import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 查询支付款项信息
 */
@Data
public class OrderPayReqVO {


    /**
     * 选择的支付款项支付款项
     * 支付款项，01：租车押金，02：违章押金， 03补付租车押金
     支付款项，06：充值,07:支付欠款
     坦客支付款项，04:行程费用，05:押金费用
     来自配置DataPayKindConstant.class
     */
    @AutoDocProperty("支付款项")
    @NotNull(message = "payKind不能为空")
    private List<String> payKind;

    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    @NotNull(message = "会员号不能为空")
    private String menNo;
    /**
     * orderNo
     */
    @AutoDocProperty("主订单号")
    @NotNull(message = "主订单号不能为空")
    private String orderNo;

    /**
     * 是否使用钱包 0-否，1-是
     */
    @AutoDocProperty("是否使用钱包 0-否，1-是")
    private Integer isUseWallet;
    
    
    // ----------------- 获取收银台参数 -----------------
	private String internalNo = "0";  //默认0
	//来源
	private String atappId;
	private String payType;
	
	
    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMenNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(!CollectionUtils.isEmpty(getPayKind()), ErrorCode.PARAMETER_ERROR.getText());
    }

}
