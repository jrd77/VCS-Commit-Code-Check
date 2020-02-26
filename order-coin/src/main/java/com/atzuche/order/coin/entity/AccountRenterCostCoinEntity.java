package com.atzuche.order.coin.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客订单使凹凸币流水
 * 
 * @author ZhangBin
 * @date 2020-01-07 20:23:33
 * @Description:
 */
@Data
public class AccountRenterCostCoinEntity implements Serializable {
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
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 
	 */
	private String memNo;
	/**
	 * 使用方式：1
	 */
	private Integer orderType;
	/**
	 * 凹凸币金额
	 */
	private Integer amt;
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 创建人
	 */
	private String createOp;

	/**
	 * 修改人
	 */
	private String updateOp;


}
