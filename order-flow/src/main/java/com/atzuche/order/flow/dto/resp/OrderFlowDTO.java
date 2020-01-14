package com.atzuche.order.flow.dto.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;



@Data
@ToString
public class OrderFlowDTO implements Serializable {

	@AutoDocProperty(value = "订单号")
	private String orderNo;

	@AutoDocProperty(value = "状态描述")
	private String orderStatusDesc;

	@AutoDocProperty(value = "来源，如管理后台")
	private String source;

	@AutoDocProperty(value = "创建时间")
	private LocalDateTime createTime;


}
