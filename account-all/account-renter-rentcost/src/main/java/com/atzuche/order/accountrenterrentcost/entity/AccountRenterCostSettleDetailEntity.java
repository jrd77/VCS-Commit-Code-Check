package com.atzuche.order.accountrenterrentcost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租车费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 * @Description:
 */
@Data
public class AccountRenterCostSettleDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Long orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 金额
	 */
	private Integer amt;
	/**
	 * 费用编码
	 */
	private Integer costCode;
	/**
	 * 费用描述
	 */
	private String costDetail;
	/**
	 * 费用凭证
	 */
	private String uniqueNo;
	/**
	 * 费用类型
	 */
	private Integer type;
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
