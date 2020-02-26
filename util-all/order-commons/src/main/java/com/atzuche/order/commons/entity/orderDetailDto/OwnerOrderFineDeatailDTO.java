package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;


/**
 * 车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2020-01-09 11:30:07
 * @Description:
 */
@Data
public class OwnerOrderFineDeatailDTO implements Serializable {
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
	 * 罚金金额
	 */
	private Integer fineAmount;
	/**
	 * 罚金补贴方编码（车主/租客/平台）1-租客，2-车主，3-平台
	 */
	private String fineSubsidyCode;
	/**
	 * 罚金补贴描述
	 */
	private String fineSubsidyDesc;
	/**
	 * 罚金来源编码（车主/租客/平台）1-租客，2-车主，3-平台
	 */
	private String fineSubsidySourceCode;
	/**
	 * 罚金来源描述
	 */
	private String fineSubsidySourceDesc;
	/**
	 * 罚金类型：1-车主修改交接车地址罚金，4-取消订单违约金
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
