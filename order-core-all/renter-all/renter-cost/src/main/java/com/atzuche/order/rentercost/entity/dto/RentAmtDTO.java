package com.atzuche.order.rentercost.entity.dto;

import java.util.List;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RentAmtDTO {

	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
     * 一天一价
     */
	private List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;
}
