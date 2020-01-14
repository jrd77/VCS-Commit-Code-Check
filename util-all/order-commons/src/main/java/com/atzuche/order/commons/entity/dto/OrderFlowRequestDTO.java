package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;


@Data
@ToString
public class OrderFlowRequestDTO implements Serializable {

	@AutoDocProperty(value = "订单号")
	@NotBlank(message = "订单号不能为空")
	private String orderNo;

}
