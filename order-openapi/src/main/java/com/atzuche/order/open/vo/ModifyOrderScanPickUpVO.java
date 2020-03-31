package com.atzuche.order.open.vo;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderScanPickUpVO {

	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="ownerMemNo不能为空")
	@AutoDocProperty(value="ownerMemNo,必填，",required=true)
	private String ownerMemNo;
}
