package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;


/**
 * 租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-23 11:44:27
 * @Description:
 */
@Data
public class RenterOrderFineDeatailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 罚金金额
	 */
	private Integer fineAmount;
	/**
	 * 罚金补贴方编码（车主/租客/平台）
	 */
	private String fineSubsidyCode;
	/**
	 * 罚金补贴描述
	 */
	private String fineSubsidyDesc;
	/**
	 * 罚金来源编码（车主/租客/平台）
	 */
	private String fineSubsidySourceCode;
	/**
	 * 罚金来源描述
	 */
	private String fineSubsidySourceDesc;
	/**
	 * 罚金类型
	 */
	private Integer fineType;
	/**
	 * 罚金类型描述
	 */
	private String fineTypeDesc;
	/**
	 * 操作人ID
	 */
	private String operatorId;
	/**
	 * 操作人名称
	 */
	private String operator;
	/**
	 * 部门ID
	 */
	private Integer deptId;
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 备注
	 */
	private String remark;

}
