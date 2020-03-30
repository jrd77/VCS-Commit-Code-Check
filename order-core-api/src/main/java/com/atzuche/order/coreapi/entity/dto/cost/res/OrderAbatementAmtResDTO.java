package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

import java.util.List;

/**
 * 全面保障服务费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 15:11
 */

@Data
public class OrderAbatementAmtResDTO {


    /**
     * 全面保障服务费
     */
    private Integer abatementAmt;

    /**
     * 全面保障服务费明细
     */
    private List<RenterOrderCostDetailEntity> details;
}
