package com.atzuche.order.renterorder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 订单券表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:45:08
 * @Description:
 */
@Data
public class OrderCouponEntity implements Serializable {
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
	 * 优惠券id
	 */
	private String couponId;
	/**
	 * 优惠券名称
	 */
	private String couponName;
	/**
	 * 优惠券类型
	 */
	private Integer couponType;
	/**
	 * 状态 0：未使用 1：已使用
	 */
	private Integer status;
	/**
	 * 描述信息
	 */
	private String couponDesc;
	/**
	 * 抵扣的金额
	 */
	private Integer amount;
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
