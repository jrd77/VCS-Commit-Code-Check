/**
 * 
 */
package com.atzuche.order.coreapi.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@Data
@ToString
public class OrderSecondOpenEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 车辆号
	 */
	private String carNo;
	/**
	 * 车牌号
	 */
	private String plateNum;
	/**
	 * 车辆类型
	 */
	private String carOwnerType;
	/**
	 * 车主手机号
	 */
	private String ownerPhone;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 订单开始时间
	 */
	private String rentTime;
	/**
	 * 订单结束时间
	 */
	private String revertTime;
	/**
	 * ,触发点：下单，换车，结算
	 */
	private String savePoint;
	/**
	 * ,车主的会员号
	 */
	private String ownerNo;
	/**
	 * ,0未开户，1已开户
	 */
	private Integer isOpenVir;
	
	/**
	 * 扩展字段1
	 */
	private String ext1;
	/**
	 * 扩展字段2
	 */
	private String ext2;
	/**
	 * 扩展字段3
	 */
	private String ext3;
	/**
	 * 扩展字段4
	 */
	private String ext4;
	/**
	 * 扩展字段5
	 */
	private String ext5;
	
	
}
