package com.atzuche.order.rentercost.entity.vo;

import lombok.Data;

@Data
public class PayableVO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 唯一编码
	 */
	private String uniqueNo;
	/**
	 * 类型：1-app修改点单的应付对应uniqueNo=renterOrderNo,2-管理后台补付对应uniqueNo=id
	 */
	private Integer type;
	/**
	 * 名称
	 */
	private String title;
	/**
	 * 金额
	 */
	private Integer amt;
}
