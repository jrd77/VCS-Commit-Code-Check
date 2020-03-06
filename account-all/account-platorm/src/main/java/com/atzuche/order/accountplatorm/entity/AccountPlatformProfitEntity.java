package com.atzuche.order.accountplatorm.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 *  平台订单收益结算表
 * 
 * @author ZhangBin
 * @date 2020-03-06 14:08:27
 * @Description:
 */
@Data
public class AccountPlatformProfitEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 车主子单号
	 */
	private String ownerOrderNo;
	/**
	 * 车主补贴金额
	 */
	private Integer ownerSubsidyAmt;
	/**
	 * 补贴车主金额
	 */
	private Integer subsidyOwnerAmt;
	/**
	 * 车主收益金额
	 */
	private Integer ownerIncomeAmt;
	/**
	 * 租客费用
	 */
	private Integer renterCost;
	/**
	 * 租客补贴金额
	 */
	private Integer renterSubsidyAmt;
	/**
	 * 补贴租客金额
	 */
	private Integer subsidyRenterAmt;
	/**
	 * 平台补贴
	 */
	private Integer platformSubsidyAmt;
	/**
	 * 平台应收费用金额
	 */
	private Integer platformReceivableAmt;
	/**
	 * 平台实收费用金额
	 */
	private Integer platformReceivedAmt;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 状态（违章结算0/订单结算1/取消订单2）
	 */
	private Integer status;
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
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
