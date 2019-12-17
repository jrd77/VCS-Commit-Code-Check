package com.atzuche.order.accountownerincome.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主收益待审核表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 * @Description:
 */
@Data
public class AccountOwnerIncomeExamineEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 收益审核金额
	 */
	private Integer amt;
	/**
	 * 收益审核描述
	 */
	private String detail;
	/**
	 * 审核状态
	 */
	private Integer status;
	/**
	 * 审核人
	 */
	private String opName;
	/**
	 * 审核时间
	 */
	private LocalDateTime time;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 更新人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
