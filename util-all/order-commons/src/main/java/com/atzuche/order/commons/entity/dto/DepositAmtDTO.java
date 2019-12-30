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
	 * 车辆残值
	 */
	private Integer surplusPrice;
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
