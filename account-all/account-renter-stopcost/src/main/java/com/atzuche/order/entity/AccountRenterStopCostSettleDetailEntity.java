package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 停运费结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 * @Description:
 */
@Data
public class AccountRenterStopCostSettleDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
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
	 * 费用单价（负）
	 */
	private Integer price;
	/**
	 * 费用天数
	 */
	private Integer days;
	/**
	 * 费用金额（负）
	 */
	private Integer amt;
	/**
	 * 费用来源编码
	 */
	private Integer sourceCode;
	/**
	 * 费用来源描述
	 */
	private String sourceDetail;
	/**
	 * 入账凭证
	 */
	private String uniqueNo;
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
