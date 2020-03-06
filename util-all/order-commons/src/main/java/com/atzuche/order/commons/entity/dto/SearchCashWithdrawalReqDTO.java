package com.atzuche.order.commons.entity.dto;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchCashWithdrawalReqDTO {
	@NotBlank(message="memNo不能为空")
	private String memNo;
}
