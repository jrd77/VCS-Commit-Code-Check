package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 * @Description:
 */
@Data
public class CashierRefundApplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Integer orderNo;
	/**
	 * 退款类型来源code
	 */
	private Integer refundTypeSourceCode;
	/**
	 * 退款类型来源描述
	 */
	private String refundTypeSourceDetail;
	/**
	 * 退款金额
	 */
	private Integer amt;
	/**
	 * 退款状态
	 */
	private Integer status;
	/**
	 * 退款外部凭证
	 */
	private String refundUniqueNo;
	/**
	 * 备注
	 */
	private String remake;
	/**
	 * 操作部门
	 */
	private String deptName;
	/**
	 * 
	 */
	private LocalDateTime createTime;
	/**
	 * 
	 */
	private String createOp;
	/**
	 * 
	 */
	private LocalDateTime updateTime;
	/**
	 * 
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
