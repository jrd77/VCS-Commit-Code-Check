package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderRefundRecordDTO implements Serializable {

    private static final long serialVersionUID = -8765614836543807673L;

    /**
	 * 
	 */
	private Integer id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 租客订单号
	 */
	private String renterOrderNo;
	/**
	 * 车主订单号
	 */
	private String ownerOrderNo;
	/**
	 * 租客扣款金额
	 */
	private Integer renterDeduct;
	/**
	 * 车主实际收益
	 */
	private Integer ownerRealIncome;
	/**
	 * 状态：0待车主确认，1已车主确认，2自动确认
	 */
	private Integer status;
	/**
	 * 收取违约金：0待车主确认，1收取，2不收取
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

}
