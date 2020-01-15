package com.atzuche.order.commons.vo.res.ownercosts;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-27 11:02:47
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
