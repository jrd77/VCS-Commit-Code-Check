package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class DepositAmtDTO {
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 城市code
	 */
	private Integer cityCode;
	/**
	 * 是否内部员工 1是，其他否
	 */
	//private Integer internalStaff;
	/**
	 * 车辆残值
	 */
	private Integer surplusPrice;
	/**
	 * 减免比例
	 */
	//private Double reliefPercetage;
	/**
	 * 车型品牌系数
	 */
	//private Double carBrandTypeRadio;
	/**
	 * 车辆年份系数
	 */
	//private Double carYearRadio;
    /**
     * 车辆品牌
     */
    private String brand;
    /**
     * 车型
     */
    private String type;
    /**
     * 行驶证注册年月
     */
    private LocalDate licenseDay;
}
