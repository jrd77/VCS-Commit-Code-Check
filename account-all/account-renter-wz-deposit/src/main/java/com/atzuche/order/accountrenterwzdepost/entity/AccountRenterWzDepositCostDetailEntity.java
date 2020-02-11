package com.atzuche.order.accountrenterwzdepost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 违章费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 * @Description:
 */
@Data
public class AccountRenterWzDepositCostDetailEntity implements Serializable {
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
	 * 车主子订单
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 支付方式code
	 */
	private Integer paymentCode;
	/**
	 * 支付方式
	 */
	private String paymentDetail;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账来源编码
	 */
	private String sourceCode;
	/**
	 * 入账来源描述
	 */
	private String sourceDetail;
	/**
	 * 交易凭证
	 */
	private String uniqueNo;
	/**
	 * 部门
	 */
	private String deptName;
	/**
	 * 操作人
	 */
	private String deptOp;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 更新时间
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
