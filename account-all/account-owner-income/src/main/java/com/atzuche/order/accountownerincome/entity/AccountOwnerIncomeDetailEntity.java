package com.atzuche.order.accountownerincome.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主收益资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:44:05
 * @Description:
 */
@Data
public class AccountOwnerIncomeDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
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
	 * 收益金额
	 */
	private Integer amt;
	/**
	 * 收益/提现时间
	 */
	private LocalDateTime time;
	/**
	 * 收益描述
	 */
	private String detail;
	/**
	 * 收益费用编码
	 */
	private String costCode;
	/**
	 * 收益费用编码描述
	 */
	private String costDetail;
	/**
	 * 收益类型（收益/提现）
	 */
	private Integer type;
	/**
	 * 提现申请id
	 */
	private Integer cashApplyId;
	/**
	 * 收益审核id
	 */
	private Integer incomeExamineId;
	/**
	 * 收银台凭证
	 */
	private String unqueNo;
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
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}