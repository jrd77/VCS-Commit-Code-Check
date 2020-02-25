package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主收益待审核表
 * 
 * @author ZhangBin
 * @date 2020-02-21 20:20:35
 * @Description:
 */
@Data
public class AccountOwnerIncomeExamineDTO implements Serializable {
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
	 * 车主子订单
	 */
	private String ownerOrderNo;
	/**
	 * 收益审核金额
	 */
	private Integer amt;
	/**
	 * 收益审核描述
	 */
	private String detail;
	/**
	 * 审核状态1,待审核 2,审核通过 3,审核拒绝
	 */
	private Integer status;
	/**
	 * 类型，1收益，2调账
	 */
	private Integer type;
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

}
