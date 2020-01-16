package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 后台管理操作费用表
 * 
 * @author ZhangBin
 * @date 2020-01-16 15:03:47
 * @Description:
 */
@Data
public class OrderConsoleCostDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 补贴费用类型 1、租金 2、取还车费用 3、保险 4、不计免赔
	 */
	private String subsidTypeName;
	/**
	 * 补贴费用类型编码
	 */
	private String subsidyTypeCode;
	/**
	 * 补贴来源方编码 1、租客 2、车主 3、平台
	 */
	private String subsidySourceCode;
	/**
	 * 补贴来源方
	 */
	private String subsidySourceName;
	/**
	 * 补贴方编码 1、租客 2、车主 3、平台
	 */
	private String subsidyTargetCode;
	/**
	 * 补贴方名称
	 */
	private String subsidyTargetName;
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

}
