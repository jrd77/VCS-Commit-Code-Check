package com.atzuche.order.coreapi.entity.bo;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class RenterModifyGetReturnTimeBO {

	/**
	 * 起始时间
	 */
	private LocalDateTime rentTime;
	/**
	 * 结束时间
	 */
	private LocalDateTime revertTime;
	
	public RenterModifyGetReturnTimeBO() {}
	
	public RenterModifyGetReturnTimeBO(LocalDateTime rentTime, LocalDateTime revertTime) {
		this.rentTime = rentTime;
		this.revertTime = revertTime;
	}
}
