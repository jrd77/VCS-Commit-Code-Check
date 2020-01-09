package com.atzuche.order.accountownercost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2020-01-09 10:32:27
 * @Description:
 */
@Data
public class AccountOwnerCostSettleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 车主订单号
	 */
	private String orderNo;
	/**
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 车主补贴费用
	 */
	private Integer subsidyAmt;
	/**
	 * 采购费用
	 */
	private Integer purchaseAmt;
	/**
	 * 增值订单费用
	 */
	private Integer incrementAmt;
	/**
	 * 车主端代管车服务费
	 */
	private Integer proxyExpenseAmt;
	/**
	 * 车主端平台服务费
	 */
	private Integer serviceExpenseAmt;
	/**
	 * gps服务费
	 */
	private Integer gpsAmt;
	/**
	 * 车主油费
	 */
	private Integer oilAmt;
	/**
	 * 管理后台补贴
	 */
	private Integer consoleSubsidyAmt;
	/**
	 * 车主罚金
	 */
	private Integer fineAmt;
	/**
	 * 管理后台全局罚金
	 */
	private Integer consoleFineAmt;
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
	 * update_op
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
