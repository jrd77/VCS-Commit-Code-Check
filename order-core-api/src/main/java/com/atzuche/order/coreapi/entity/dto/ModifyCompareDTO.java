package com.atzuche.order.coreapi.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class ModifyCompareDTO {
	private LocalDateTime rentTime;
	private LocalDateTime revertTime;
	private String getAddr;
	private String getLon;
	private String getLat;
	private String returnAddr;
	private String returnLon;
	private String returnLat;
}
