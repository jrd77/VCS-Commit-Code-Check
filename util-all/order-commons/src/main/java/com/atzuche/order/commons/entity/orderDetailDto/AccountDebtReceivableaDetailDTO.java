package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 个人历史欠款收款记录
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:13:27
 * @Description:
 */
@Data
public class AccountDebtReceivableaDetailDTO implements Serializable {
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
	 * 欠款主订单号
	 */
	private String orderNo;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账时间
	 */
	private LocalDateTime time;
	/**
	 * 收款来源编码描述
	 */
	private String sourceCode;
	/**
	 * 收款来源编码（收银台/非收银台）
	 */
	private String sourceDetail;
	/**
	 * 收款凭证
	 */
	private String uniqueNo;
	/**
	 * 历史欠款id
	 */
	private Integer debtDetailId;
	/**
	 * 操作部门名称
	 */
	private String deptName;
	/**
	 * 备注
	 */
	private String remark;


}
