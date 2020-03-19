package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.autoyol.doc.annotation.AutoDocProperty;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 个人代付信息
 * @author haibao.yan
 */
@ToString
@Data
public class AccountPayAbleResVO {
    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 支付标题
     */
    @AutoDocProperty("支付标题")
    private String title;

    /**
     * 代付款项
     */
    @AutoDocProperty("代付款项")
    private Integer amt;
    /**
     * 费用类型code
     */
    @AutoDocProperty("费用类型code")
    private RenterCashCodeEnum renterCashCode;

    /**
     * 支持的支付方式   （默认暂不支持预授权支付）
     */
    @AutoDocProperty("支持的支付方式 （默认暂不支持预授权支付）")
    private List<String> payTypes;
    
	/**
	 * 唯一编码
	 */
	private String uniqueNo;

    private AccountPayAbleResVO(){}

    public AccountPayAbleResVO(String orderNo, String memNo, Integer amt, RenterCashCodeEnum renterCashCode,String title) {
        this.orderNo = orderNo;
        this.memNo = memNo;
        this.amt = amt;
        this.renterCashCode = renterCashCode;
        this.title = title;
        this.payTypes = ImmutableList.of(PayTypeEnum.PAY_PUR.getCode());
    }
    
    public AccountPayAbleResVO(String orderNo, String memNo, Integer amt, RenterCashCodeEnum renterCashCode,String title,String uniqueNo) {
        this.orderNo = orderNo;
        this.memNo = memNo;
        this.amt = amt;
        this.renterCashCode = renterCashCode;
        this.title = title;
        this.payTypes = ImmutableList.of(PayTypeEnum.PAY_PUR.getCode());
        this.uniqueNo = uniqueNo;
    }
    

}
