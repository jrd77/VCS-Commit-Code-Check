package com.atzuche.order.commons.vo.req;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderConsoleCheckReq {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@AutoDocProperty(value="取车时间,格式 yyyyMMddHHmmss",required=true)
	private String rentTime;
	
	@AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss",required=true)
	private String revertTime;
}
