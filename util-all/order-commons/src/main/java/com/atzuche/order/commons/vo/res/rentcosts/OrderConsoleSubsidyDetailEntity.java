package com.atzuche.order.commons.vo.res.rentcosts;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 后台管理操补贴明细表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2020-01-01 11:01:58
 * @Description:
 */
@Data
public class OrderConsoleSubsidyDetailEntity implements Serializable {
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
	 * 补贴费用类型名称 如：租金 、取还车费用
	 */
	private String subsidyTypeName;
	/**
	 * 补贴费用类型编码
	 */
	private String subsidyTypeCode;
	/**
	 * 补贴来源方 1、租客 2、车主 3、平台
	 */
	private String subsidySourceName;
	/**
	 * 补贴来源方编码
	 */
	private String subsidySourceCode;
	/**
	 * 补贴方名称 1、租客 2、车主 3、平台
	 */
	private String subsidyTargetName;
	/**
	 * 补贴方编码
	 */
	private String subsidyTargetCode;
	/**
	 * 补贴费用项名称 如：凹凸比、优惠券等
	 */
	private String subsidyCostName;
	/**
	 * 补贴费用项编码
	 */
	private String subsidyCostCode;
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
