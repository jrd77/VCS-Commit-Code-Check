package com.atzuche.order.admin.vo.req;

import javax.validation.constraints.NotNull;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DelSupplementReqVO {
	@NotNull(message="id不能为空")
	@AutoDocProperty(value="订单编号",required=true)
	private Integer id;
}
