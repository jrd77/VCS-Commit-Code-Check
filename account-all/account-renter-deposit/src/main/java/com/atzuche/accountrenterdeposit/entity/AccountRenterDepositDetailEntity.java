package com.atzuche.accountrenterdeposit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租车押金资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 * @Description:
 */
@Data
public class AccountRenterDepositDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 支付方式
	 */
	private Integer payment;
	/**
	 * 支付渠道
	 */
	private Integer paymentChannel;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 预授权金额
	 */
	private Integer authorizeDepositAmt;
	/**
	 * 预授权到期时间
	 */
	private LocalDateTime authorizeExpireTime;
	/**
	 * 押金来源编码
	 */
	private Integer sourceCode;
	/**
	 * 押金来源编码描述
	 */
	private String sourceDetail;
	/**
	 * 押金凭证
	 */
	private String uniqueNo;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 更新时间
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
