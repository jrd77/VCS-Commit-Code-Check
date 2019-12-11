package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
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
	 * 车主订单号
	 */
	private Integer orderNo;
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
	 * 应收费用
	 */
	private Integer yingshouAmt;
	/**
	 * amt
	 */
	private Integer shishouAmt;
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
