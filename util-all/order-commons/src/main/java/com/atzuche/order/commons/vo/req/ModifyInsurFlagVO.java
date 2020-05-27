package com.atzuche.order.commons.vo.req;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyInsurFlagVO {
	
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	@NotBlank(message="buyKey不能为空")
	@AutoDocProperty(value="abatementFlag:购买补充全险,tyreInsurFlag:购买轮胎保障服务,driverInsurFlag:购买驾乘无忧保障服务")
	private String buyKey;
	@NotNull(message="buyValue不能为空")
	@AutoDocProperty(value="0-不购买，1-购买")
	private Integer buyValue;
}
