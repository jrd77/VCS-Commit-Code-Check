package com.atzuche.order.commons.vo.delivery;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SimpleOrderInfoVO {
	private String cityCode;
	private String renterMemNo;
	private String ownerMemNo;
}
