package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租车费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 * @Description:
 */
@Data
public class AccountRenterCostDetailEntity implements Serializable {
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
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 支付来源code
	 */
	private Integer paySourceCode;
	/**
	 * 支付来源
	 */
	private String paySource;
	/**
	 * 支付方式code
	 */
	private Integer paymentCode;
	/**
	 * 支付方式
	 */
	private String payment;
	/**
	 * 支付渠道code
	 */
	private Integer payChannelCode;
	/**
	 * 支付渠道
	 */
	private String payChannel;
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
	 * 交易时间
	 */
	private LocalDateTime transTime;
	/**
	 * 入账时间
	 */
	private LocalDateTime time;
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
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
