package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

/**
 * 计算超运能溢价参数
 * @author pengcheng.fu
 * @date 2020/3/27 16:48
 */
@Data
public class OrderCostGetReturnCarOverCostReqDTO {

    /**
     * 城市code
     */
    private Integer cityCode;
    /**
     * 订单类型:1,短租订单 2,平台套餐订单
     */
    private Integer orderCategory;
    /**
     * 是否计算取车费用
     */
    private Boolean isGetCarCost;
    /**
     * 是否计算还车费用
     */
    private Boolean isReturnCarCost;
}
