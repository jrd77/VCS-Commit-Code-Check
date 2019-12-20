package com.atzuche.order.commons.entity.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExtraDriverDTO {
	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 附加驾驶人id列表
	 */
	private List<String> driverIds;
}
