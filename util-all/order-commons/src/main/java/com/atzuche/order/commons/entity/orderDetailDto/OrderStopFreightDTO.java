package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderStopFreightDTO {
	
	private Integer agreementStopFreightRate;

    private Integer notagreementStopFreightRate;

    private Integer agreementStopFreightPrice;

    private Integer notagreementStopFreightPrice;
}
