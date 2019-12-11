package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 停运费资金进出明细
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 * @Description:
 */
@Data
public class AccountRenterStopCostDetailEntity implements Serializable {
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
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账时间
	 */
	private LocalDateTime time;
	/**
	 * 入账来源编码
	 */
	private Integer sourceCode;
	/**
	 * 入账来源描述
	 */
	private String sourceDetail;
	/**
	 * 入账凭证
	 */
	private String uniqueNo;
	/**
	 * 支付方式编码
	 */
	private Integer paymentCode;
	/**
	 * 支付方式
	 */
	private String payment;
	/**
	 * 收银台凭证
	 */
	private String cashierUniqueNo;
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 更新操作人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
