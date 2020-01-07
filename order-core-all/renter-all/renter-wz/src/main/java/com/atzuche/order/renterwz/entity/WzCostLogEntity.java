package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * WzCostLogEntity
 *
 * @author shisong
 * @date 2020/01/06
 */
@Data
public class WzCostLogEntity{
	/**
	*违章费用日志表主键
	*/
	private Long id;
	/**
	*订单号
	*/
	private String orderNo;
	/**
	*费用编码
	*/
	private String costCode;
	/**
	*内容
	*/
	private String content;
	/**
	*操作人
	*/
	private String operator;
	/**
	*创建时间
	*/
	private Date createTime;
}
