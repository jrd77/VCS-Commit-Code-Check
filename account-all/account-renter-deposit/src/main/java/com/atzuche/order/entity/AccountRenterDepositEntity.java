package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租车押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 * @Description:
 */
@Data
public class AccountRenterDepositEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Long orderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 支付状态
	 */
	private Integer payStatus;
	/**
	 * 支付时间
	 */
	private LocalDateTime payTime;
	/**
	 * 结算状态
	 */
	private Integer settleStatus;
	/**
	 * 结算时间
	 */
	private LocalDateTime settleTime;
	/**
	 * 应付押金总额
	 */
	private Integer yingfuDepositAmt;
	/**
	 * 实付押金总金额
	 */
	private Integer shifuDepositAmt;
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
	 * 开启免押
	 */
	private Integer isFreeDeposit;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
