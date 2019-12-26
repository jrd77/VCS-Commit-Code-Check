package com.atzuche.order.commons.entity.dto;

import lombok.Data;

@Data
public class GetReturnCarOverCostReqDto {
    /**
     * 基本信息
     */
    private CostBaseDTO costBaseDTO;

    /**
     * 城市code
     */
    private Integer cityCode;
    /**
     * 订单类型:1,短租订单 2,平台套餐订单
     */
    Integer orderType;

}
