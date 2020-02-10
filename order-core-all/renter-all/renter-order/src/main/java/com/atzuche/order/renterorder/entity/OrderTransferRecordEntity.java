package com.atzuche.order.renterorder.entity;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderTransferRecordEntity {
	private Integer id;
	private String orderNo;
	private String memNo;
	private String carNo;
	private Integer source;
	private String operator;
	private String carPlateNum;
	private LocalDateTime createTime;
}
