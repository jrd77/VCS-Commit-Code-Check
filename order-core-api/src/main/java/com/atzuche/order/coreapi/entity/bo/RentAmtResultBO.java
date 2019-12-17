package com.atzuche.order.coreapi.entity.bo;

import java.util.List;

import com.autoyol.platformcost.model.CarPriceOfDay;

import lombok.Data;

@Data
public class RentAmtResultBO {

	/**
	 * 单价
	 */
	private Integer unitPrice;
	/**
	 * 单位/份数
	 */
	private Double unitCount;
	/**
	 * 总价
	 */
	private Integer totalFee;
	/**
	 * 新的商品价格列表
	 */
	private List<CarPriceOfDay> carPriceOfDayList;
}
