package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;


@Data
public class WzQueryDayConfDTO {
	/**
	*主键
	*/
	private Long id;
	/*
	* 城市code
	* */
	private Integer cityCode;
	/*
	* 进入违章时间
	* */
	private Integer illegalQueryDay;
    /*
    * 结算时间
    * */
	private Integer transProcessDay;
}
