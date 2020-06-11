package com.atzuche.order.commons.entity.dto;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OtherSubsidyRenyunDTO {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	private Integer renterOtherSubsidyAmt;
	private Integer ownerOtherSubsidyAmt;
}
