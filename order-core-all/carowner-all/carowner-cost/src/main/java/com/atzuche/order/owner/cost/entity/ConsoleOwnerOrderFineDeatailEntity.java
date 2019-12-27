package com.atzuche.order.owner.cost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 全局的车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 * @Description:
 */
@Data
public class ConsoleOwnerOrderFineDeatailEntity implements Serializable {
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
