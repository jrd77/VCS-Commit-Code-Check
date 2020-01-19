package com.atzuche.order.renterorder.entity;

import lombok.Data;

@Data
public class OrderTransferRecordEntity {
	private Integer id;
	private String orderNo;
	private String memNo;
	private String carNo;
	private Integer source;
	private String operator;
	private String carPlateNum;
}
