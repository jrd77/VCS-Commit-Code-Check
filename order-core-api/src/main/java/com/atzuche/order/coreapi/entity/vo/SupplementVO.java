package com.atzuche.order.coreapi.entity.vo;

import java.util.List;

import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;

import lombok.Data;

@Data
public class SupplementVO {
	/**
	 * 结算状态：0-租车费用/车辆押金未结算，1-租车费用/车辆押金已结算，违章押金未结算，2-违章押金已结算 / 违章押金已暂扣，3-订单已取消
	 */
	private Integer orderSettle;
	/**
	 * 结算状态文案
	 */
	private String orderSettleTxt;
	/**
	 * 补付列表
	 */
	private List<OrderSupplementDetailEntity> list;
	
}
