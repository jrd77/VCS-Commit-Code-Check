package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * WzTemporaryRefundLogEntity
 *
 * @author shisong
 * @date 2020/01/06
 */
@Data
public class WzTemporaryRefundLogEntity{
	/**
	*违章暂扣返还日志表主键
	*/
	private Long id;
	/**
	*订单号
	*/
	private String orderNo;
	/**
	*金额
	*/
	private Integer amount;
	/**
	*原因
	*/
	private String reason;
	/**
	 * 状态0未处理 1成功 2失败
	 */
	private Integer status;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*操作人
	*/
	private String operator;
}
