package com.atzuche.order.renterorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2020-04-07 11:18:55
 * @Description:
 */
@Data
public class OwnerCouponLongEntity implements Serializable {
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
	 * 租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 租客会员号
	 */
	private String renterMemNo;
	/**
	 * 原始租金单价
	 */
	private Integer originalUnitPrice;
	/**
	 * 券编码
	 */
	private String couponCode;
	/**
	 * 实际租金单价
	 */
	private Integer actUnitPrice;
	/**
	 * 单价减免金额
	 */
	private Integer reductionPrice;
	/**
	 * 折扣比例
	 */
	private Double discounRatio;
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
