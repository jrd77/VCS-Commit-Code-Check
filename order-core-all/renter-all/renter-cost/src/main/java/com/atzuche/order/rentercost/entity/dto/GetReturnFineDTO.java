package com.atzuche.order.rentercost.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GetReturnFineDTO {

	private LocalDateTime curTime;
	private String lon;
	private String lat;
	
	public GetReturnFineDTO() {}
	
	public GetReturnFineDTO(LocalDateTime curTime, String lon, String lat) {
		this.curTime = curTime;
		this.lon = lon;
		this.lat = lat;
	}
	
}
