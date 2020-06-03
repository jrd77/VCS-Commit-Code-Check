package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberSimpleDTO {

	private Integer memNo;
	private String name;
	private Long mobile;
	
	public MemberSimpleDTO() {}

	public MemberSimpleDTO(Integer memNo, String name, Long mobile) {
		this.memNo = memNo;
		this.name = name;
		this.mobile = mobile;
	}
	
}
