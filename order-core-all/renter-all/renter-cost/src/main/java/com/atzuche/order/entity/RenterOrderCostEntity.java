package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客订单费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:10:06
 * @Description:
 */
@Data
public class RenterOrderCostEntity implements Serializable {
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
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 租车费用
	 */
	private Integer rentCarAmount;
	/**
	 * 佣金费用
	 */
	private Integer commissionAmount;
	/**
	 * 基础保障费用
	 */
	private Integer basicEnsureAmount;
	/**
	 * 全面保障费用
	 */
	private Integer comprehensiveEnsureAmount;
	/**
	 * 附加驾驶人保障费用
	 */
	private Integer additionalDrivingEnsureAmount;
	/**
	 * 其他费用
	 */
	private Integer otherAmount;
	/**
	 * 平台补贴费用
	 */
	private Integer platformSubsidyAmount;
	/**
	 * 车主补贴费用
	 */
	private Integer carOwnerSubsidyAmount;
	/**
	 * 版本号
	 */
	private Integer version;
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
