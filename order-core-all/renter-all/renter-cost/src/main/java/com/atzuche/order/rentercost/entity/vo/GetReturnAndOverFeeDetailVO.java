package com.atzuche.order.rentercost.entity.vo;


import com.atzuche.order.rentercost.entity.dto.GetReturnCostDTO;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverCostDTO;

import lombok.Data;

@Data
public class GetReturnAndOverFeeDetailVO {

	private GetReturnCostDTO getReturnCostDTO;
	private GetReturnOverCostDTO getReturnOverCostDTO;
}
