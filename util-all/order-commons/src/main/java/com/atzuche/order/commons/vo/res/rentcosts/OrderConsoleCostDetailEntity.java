package com.atzuche.order.commons.vo.res.rentcosts;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 后台管理操作费用表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2020-03-02 16:31:58
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
	 * 费用类型 1、租金 2、取还车费用 3、保险 4、不计免赔
	 */
	private String subsidTypeName;
	/**
	 * 费用类型编码
	 */
	private String subsidyTypeCode;
	/**
	 * 费用来源方编码 1、租客 2、车主 3、平台
	 */
	private String subsidySourceCode;
	/**
	 * 费用来源方
	 */
	private String subsidySourceName;
	/**
	 * 费用方编码 1、租客 2、车主 3、平台
	 */
	private String subsidyTargetCode;
	/**
	 * 费用方名称
	 */
	private String subsidyTargetName;
	/**
	 * 费用项名称 如：凹凸比、优惠券等
	 */
	private String subsidyCostName;
	/**
	 * 费用项编码
	 */
	private String subsidyCostCode;
	/**
	 * 费用描述
	 */
	private String subsidyDesc;
	/**
	 * 费用金额
	 */
	private Integer subsidyAmount;
	/**
	 * 费用凭证
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
