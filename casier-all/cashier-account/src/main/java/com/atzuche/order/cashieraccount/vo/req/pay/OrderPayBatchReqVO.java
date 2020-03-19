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
public class OrderPayBatchReqVO {
	/**
     * 选择的支付款项支付款项
     * 支付款项，01：租车押金，02：违章押金， 03补付租车押金
     支付款项，06：充值,07:支付欠款
     坦客支付款项，04:行程费用，05:押金费用
     来自配置DataPayKindConstant.class
     
     11支付费用 ,08支付管理后台补付,07支付欠款
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
    @AutoDocProperty("订单号集合")
    @NotNull(message = "订单号集合不能为空")
    private List<String> orderNos;

    /**
     * 是否使用钱包 0-否，1-是   去掉。
     */
//    @AutoDocProperty("是否使用钱包 0-否，1-是")
//    private Integer isUseWallet = new Integer(0);  //默认0
    
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
        Assert.notNull(getOrderNos(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(!CollectionUtils.isEmpty(getPayKind()), ErrorCode.PARAMETER_ERROR.getText());
    }
}
