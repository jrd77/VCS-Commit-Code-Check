package com.atzuche.order.accountownercost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;


/**
 * 车主费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 16:33:28
 * @Description:
 */
@ToString
@Data
public class AccountOwnerCostSettleDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 金额
	 */
	private Integer amt;
	/**
	 * 费用编码
	 */
	private String sourceCode;
	/**
	 * 费用来源描述
	 */
	private String sourceDetail;
	/**
	 * 费用唯一凭证
	 */
	private String uniqueNo;
	/**
	 * 费用类型
	 */
	private Integer costType;
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
	 * 
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}