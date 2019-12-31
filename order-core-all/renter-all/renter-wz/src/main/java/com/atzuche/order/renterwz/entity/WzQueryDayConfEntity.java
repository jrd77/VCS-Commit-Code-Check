package com.atzuche.order.renterwz.entity;

import lombok.Data;

/**
 * WzQueryDayConfEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class WzQueryDayConfEntity{
	/**
	*主键
	*/
	private Long id;
	private Integer cityCode;
	private Integer illegalQueryDay;
	private Integer transProcessDay;
}
