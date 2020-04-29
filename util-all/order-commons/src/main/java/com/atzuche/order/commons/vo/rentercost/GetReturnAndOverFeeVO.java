package com.atzuche.order.commons.vo.rentercost;

import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;

import lombok.Data;

@Data
public class GetReturnAndOverFeeVO {
	// 取还车费用
	private GetReturnCarCostReqDto getReturnCarCostReqDto;
	// 高峰运能用
	private GetReturnCarOverCostReqDto getReturnCarOverCostReqDto;
}
