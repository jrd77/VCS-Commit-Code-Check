package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CarMemNoReqVO {
	@AutoDocProperty(value="memNo,必填")
	@NotNull(message="会员号不能为空")
	private Integer ownerNo;
}
