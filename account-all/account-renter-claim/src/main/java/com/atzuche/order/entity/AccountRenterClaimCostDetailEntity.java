package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 理赔费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 * @Description:
 */
@Data
public class AccountRenterClaimCostDetailEntity implements Serializable {
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
	 * 费用来源编码
	 */
	private Integer sourceCode;
	/**
	 * 费用来源描述
	 */
	private String sourceDetail;
	/**
	 * 入账金额
	 */
	private Integer amt;
	/**
	 * 入账时间
	 */
	private LocalDateTime time;
	/**
	 * 收银凭证
	 */
	private String uniqueNo;
	/**
	 * 操作部门名称
	 */
	private String deptName;
	/**
	 * 操作人
	 */
	private String deptOp;
	/**
	 * 备注
	 */
	private String remake;
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
