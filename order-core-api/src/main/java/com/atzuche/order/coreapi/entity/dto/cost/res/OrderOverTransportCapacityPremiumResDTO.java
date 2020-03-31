package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

import java.util.List;

/**
 * 超运能溢价
 *
 * @author pengcheng.fu
 * @date 2020/3/3016:27
 */

@Data
public class OrderOverTransportCapacityPremiumResDTO {


    /**
     * 取车服务溢价费用
     */
    private Integer getCarOverCost;

    /**
     * 还车服务溢价费用
     */
    private Integer returnCarOverCost;

    /**
     * 取还车超运能费用明细
     */
    private List<RenterOrderCostDetailEntity> details;

}
