package com.atzuche.order.coreapi.entity.bo;

import java.time.LocalDateTime;
import java.util.List;

import com.atzuche.order.entity.RenterGoodsPriiceDetailEntity;

import lombok.Data;

@Data
public class RenterGoodsPriceBO {
	/**
	 * 排序使用
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
	 * 预计起租时间
	 */
	private LocalDateTime expRentStartTime;
	/**
	 * 预计还车时间
	 */
	private LocalDateTime expRentEndTime;
	/**
	 * 商品价格列表
	 */
	private List<RenterGoodsPriiceDetailEntity> renterGoodsPriceList;
	
}
