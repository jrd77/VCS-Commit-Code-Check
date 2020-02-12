package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;


/**
 * 车主费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 * @Description:
 */
@Data
public class OwnerOrderCostDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 增值费用
	 */
	private Integer incrementAmount;
	/**
	 * 补贴费用
	 */
	private Integer subsidyAmount;
	/**
	 * 采购费用(代收代付的租金)
	 */
	private Integer purchaseAmount;
	/**
	 * 版本号
	 */
	private Integer version;


}
