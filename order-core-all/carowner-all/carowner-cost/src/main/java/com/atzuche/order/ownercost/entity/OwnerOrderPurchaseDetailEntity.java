package com.atzuche.order.ownercost.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主端采购费用明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 * @Description:
 */
@Data
public class OwnerOrderPurchaseDetailEntity implements Serializable {
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
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 费用编码费用编码（租金）
	 */
	private String costCode;
	/**
	 * 费用描述
	 */
	private String costCodeDesc;
	/**
	 * 单价
	 */
	private Integer unitPrice;
	/**
	 * 份数
	 */
	private Double count;
	/**
	 * 金额
	 */
	private Integer totalAmount;
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
