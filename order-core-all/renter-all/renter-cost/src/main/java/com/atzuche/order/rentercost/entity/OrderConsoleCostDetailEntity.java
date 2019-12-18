package com.atzuche.order.rentercost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 后台管理操补贴明细表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2019-12-18 14:37:56
 * @Description:
 */
@Data
public class OrderConsoleCostDetailEntity implements Serializable {
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
	 * 部门ID
	 */
	private Integer deptId;
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 操作人ID
	 */
	private String operatorId;
	/**
	 * 操作时间
	 */
	private LocalDateTime createTime;
	/**
	 * 操作备注
	 */
	private String remark;
	/**
	 * 操作人名称
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
