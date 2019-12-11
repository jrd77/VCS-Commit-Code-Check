package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 违章押金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 * @Description:
 */
@Data
public class AccountRenterWzDepositDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Integer orderNo;
	/**
	 * 
	 */
	private Integer memNo;
	/**
	 * 支付方式
	 */
	private Integer payment;
	/**
	 * 支付渠道
	 */
	private Integer payChannel;
	/**
	 * 费用编码
	 */
	private Integer costCode;
	/**
	 * 费用描述
	 */
	private String costDetail;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账来源编码
	 */
	private Integer sourceCode;
	/**
	 * 入账来源编码描述
	 */
	private String sourceDetail;
	/**
	 * 预授权金额
	 */
	private Integer authorizeAmt;
	/**
	 * 预授权到期时间
	 */
	private LocalDateTime authorizeExpireTime;
	/**
	 * 收银凭证
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
	 * 
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
