package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 违章押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 * @Description:
 */
@Data
public class AccountRenterWzDepositDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 应收违章押金
	 */
	private Integer yingshouDeposit;
	/**
	 * 实收违章押金
	 */
	private Integer shishouDeposit;
	/**
	 * 是否预授权
	 */
	private Integer isAuthorize;
	
	/**
     * 预授权金额
     */
    private Integer authorizeDepositAmt;
    /**
     * 信用支付金额
     */
    private Integer creditPayAmt;
    /**
     * 剩余押金总额
     */
    private Integer surplusDepositAmt;
    /**
     * 剩余预授权金额
     */
    private Integer surplusAuthorizeDepositAmt;
    /**
     * 剩余信用支付金额
     */
    private Integer surplusCreditPayAmt;
    /**
     * 免押方式
     */
    private Integer freeDepositType;
	/**
	 * 应退押金
	 */
	private Integer shouldReturnDeposit;
	/**
	 * 实退押金
	 */
	private Integer realReturnDeposit;
	/**
	 * 结算状态
	 */
	private Integer settleStatus;
	/**
	 * 结算时间
	 */
	private LocalDateTime settleTime;
}
