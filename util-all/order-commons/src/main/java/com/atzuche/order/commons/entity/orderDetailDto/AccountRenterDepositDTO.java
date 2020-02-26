package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租车押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2020-01-11 16:41:19
 * @Description:
 */
@Data
public class AccountRenterDepositDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        @AutoDocProperty(value="主订单号",required=true)
        private String orderNo;
    	/**
         * 会员号
         */
        @AutoDocProperty(value="会员号",required=true)
        private String memNo;
    	/**
         * 支付状态 状态:00成功，01进行中，03失败
         */
        @AutoDocProperty(value="支付状态 状态:00成功，01进行中，03失败",required=true)
        private String payStatus;
    	/**
         * 支付时间
         */
        @AutoDocProperty(value="支付时间",required=true)
        private LocalDateTime payTime;
    	/**
         * 结算状态0未结算 1 已结算
         */
        @AutoDocProperty(value="结算状态0未结算 1 已结算",required=true)
        private Integer settleStatus;
    	/**
         * 结算时间
         */
        @AutoDocProperty(value="结算时间",required=true)
        private LocalDateTime settleTime;
    	/**
         * 应付押金总额
         */
        @AutoDocProperty(value="应付押金总额",required=true)
        private Integer yingfuDepositAmt;
    	/**
         * 实付押金总金额
         */
        @AutoDocProperty(value="实付押金总金额",required=true)
        private Integer shifuDepositAmt;
    	/**
         * 预授权金额
         */
        @AutoDocProperty(value="预授权金额",required=true)
        private Integer authorizeDepositAmt;
    	/**
         * 信用支付金额
         */
        @AutoDocProperty(value="信用支付金额",required=true)
        private Integer creditPayAmt;
    	/**
         * 剩余信用支付金额
         */
        @AutoDocProperty(value="剩余信用支付金额",required=true)
        private Integer surplusCreditPayAmt;
    	/**
         * 剩余押金总额
         */
        @AutoDocProperty(value="剩余押金总额",required=true)
        private Integer surplusDepositAmt;
    	/**
         * 剩余预授权金额
         */
        @AutoDocProperty(value="剩余预授权金额",required=true)
        private Integer surplusAuthorizeDepositAmt;
    	/**
         * 减免金额
         */
        @AutoDocProperty(value="减免金额",required=true)
        private Integer reductionAmt;
    	/**
         * 免押方式(1:绑卡减免,2:芝麻减免,3:消费)
         */
        @AutoDocProperty(value="免押方式(1:绑卡减免,2:芝麻减免,3:消费)",required=true)
        private Integer freeDepositType;
    						
}
