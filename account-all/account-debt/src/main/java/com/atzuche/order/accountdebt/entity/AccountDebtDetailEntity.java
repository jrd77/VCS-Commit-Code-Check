package com.atzuche.order.accountdebt.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 个人历史欠款明细
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:13:27
 * @Description:
 */
@Data
public class AccountDebtDetailEntity implements Serializable {
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
	 * 
	 */
	private String renterOrderNo;
	/**
	 * 
	 */
	private String ownerOrderNo;
	/**
	 * 类型（订单取消罚金/订单结算欠款）
	 */
	private Integer type;
	/**
	 * 当前欠款
	 */
	private Integer currentDebtAmt;
	/**
	 * 订单欠款
	 */
	private Integer orderDebtAmt;
	/**
	 * 已还欠款
	 */
	private Integer repaidDebtAmt;
	/**
	 * 历史欠款来源编码
	 */
	private Integer sourceCode;
	/**
	 * 欠款历史来源描述
	 */
	private String sourceDetail;
	/**
	 * 更新版本号
	 */
	private Integer version;
	/**
	 * 操作人部门名称
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
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
