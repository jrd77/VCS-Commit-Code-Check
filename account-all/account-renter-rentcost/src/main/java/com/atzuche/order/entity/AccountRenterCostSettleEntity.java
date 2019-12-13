package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客费用及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:53:40
 * @Description:
 */
@Data
public class AccountRenterCostSettleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Long orderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 租车费用
	 */
	private Integer rentAmt;
	/**
	 * 佣金
	 */
	private Integer yongjinAmt;
	/**
	 * 基础保障费用
	 */
	private Integer basicEnsureAmount;
	/**
	 * 全面保障费用
	 */
	private Integer comprehensiveEnsureAmount;
	/**
	 * 附加驾驶人保证费用
	 */
	private Integer additionalDrivingEnsureAmount;
	/**
	 * 其他费用
	 */
	private Integer otherAmt;
	/**
	 * 平台补贴费用
	 */
	private Integer platformSubsidyAmount;
	/**
	 * 车主补贴费用
	 */
	private Integer carOwnerSubsidyAmount;
	/**
	 * 应付费用
	 */
	private Integer yingfuAmt;
	/**
	 * 实付费用
	 */
	private Integer shifuAmt;
	/**
	 * 欠款费用
	 */
	private Integer debtAmt;
	/**
	 * 更新版本号
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
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
