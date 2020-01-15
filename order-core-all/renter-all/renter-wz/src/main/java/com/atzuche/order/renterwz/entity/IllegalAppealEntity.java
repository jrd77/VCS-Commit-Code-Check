package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * IllegalAppealEntity
 *
 * @author shisong
 * @date 2020/01/15
 */
@Data
public class IllegalAppealEntity{
	private Integer id;
	/**
	*订单号
	*/
	private String orderNo;
	/**
	*违章序号
	*/
	private String illegalNum;
	/**
	*申述内容
	*/
	private String appealContent;
	/**
	*创建时间
	*/
	private Date createTime;
}
