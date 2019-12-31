package com.atzuche.order.coreapi.entity.vo;

import java.util.List;

import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CostDeductVO {

	/**
	 * 各种券抵扣列表
	 */
	private List<OrderCouponDTO> orderCouponList;
	/**
	 * 各种抵扣的补贴列表
	 */
	private List<RenterOrderSubsidyDetailDTO> renterSubsidyList;
}
