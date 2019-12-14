package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 * @Description:
 */
@Data
public class OwnerOrderSubsidyDetailEntity implements Serializable {
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
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 补贴类型
	 */
	private Integer subsidType;
	/**
	 * 补贴来源方编码（租客/车主/平台）
	 */
	private String subsidySourceCode;
	/**
	 * 补贴来源方
	 */
	private String subsidySource;
	/**
	 * 补贴方编码（租客/车主/平台）
	 */
	private String subsidyCode;
	/**
	 * 补贴方名称
	 */
	private String subsidyName;
	/**
	 * 补贴类型编码
	 */
	private String subsidyTypeCode;
	/**
	 * 补贴描述
	 */
	private String subsidyDesc;
	/**
	 * 补贴金额
	 */
	private Integer subsidyAmount;
	/**
	 * 补贴凭证
	 */
	private String subsidyVoucher;
	/**
	 * 备注
	 */
	private String remark;
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
