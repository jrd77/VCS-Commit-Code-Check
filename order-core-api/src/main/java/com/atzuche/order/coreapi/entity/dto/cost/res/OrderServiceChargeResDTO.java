package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

/**
 * 手续费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 15:35
 */
@Data
public class OrderServiceChargeResDTO {

    /**
     * 手续费
     */
    private Integer serviceCharge;

    /**
     * 手续费明细
     */
    private RenterOrderCostDetailEntity detail;
}
