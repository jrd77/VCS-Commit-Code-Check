package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租车押金资金进出明细表
 * 
 * @author ZhangBin
 * @date 2020-01-11 16:41:19
 * @Description:
 */
@Data
public class AccountRenterDepositDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 订单号
         */
        @AutoDocProperty(value="订单号",required=true)
        private String orderNo;
    	/**
         * 会员号
         */
        @AutoDocProperty(value="会员号",required=true)
        private String memNo;
    	/**
         * 支付方式
         */
        @AutoDocProperty(value="支付方式",required=true)
        private Integer payment;
    	/**
         * 支付渠道
         */
        @AutoDocProperty(value="支付渠道",required=true)
        private Integer paymentChannel;
    	/**
         * 入账金额
         */
        @AutoDocProperty(value="入账金额",required=true)
        private Integer amt;
    	/**
         * 预授权金额
         */
        @AutoDocProperty(value="预授权金额",required=true)
        private Integer authorizeDepositAmt;
    	/**
         * 预授权到期时间
         */
        @AutoDocProperty(value="预授权到期时间",required=true)
        private LocalDateTime authorizeExpireTime;
    	/**
         * 信用支付金额
         */
        @AutoDocProperty(value="信用支付金额",required=true)
        private Integer creditPayAmt;
    	/**
         * 信用支付到期时间
         */
        @AutoDocProperty(value="信用支付到期时间",required=true)
        private LocalDateTime creditPayExpireTime;
    	/**
         * 押金来源编码
         */
        @AutoDocProperty(value="押金来源编码",required=true)
        private String sourceCode;
    	/**
         * 押金来源编码描述
         */
        @AutoDocProperty(value="押金来源编码描述",required=true)
        private String sourceDetail;
    	/**
         * 押金凭证
         */
        @AutoDocProperty(value="押金凭证",required=true)
        private String uniqueNo;
    					
}
