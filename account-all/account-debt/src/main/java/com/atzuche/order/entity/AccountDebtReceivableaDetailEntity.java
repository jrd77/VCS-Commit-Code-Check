package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 个人历史欠款收款记录
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 * @Description:
 */
@Data
public class AccountDebtReceivableaDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 主订单号
	 */
	private Integer orderNo;
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
	private Integer sourceCode;
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
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 操作人
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
