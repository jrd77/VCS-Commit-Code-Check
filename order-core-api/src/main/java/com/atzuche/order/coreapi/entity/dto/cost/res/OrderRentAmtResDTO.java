package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

import java.util.List;

/**
 * 租金信息
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:30
 */

@Data
public class OrderRentAmtResDTO {

    /**
     * 租金
     */
    private Integer rentAmt;

    /**
     * 日均价
     */
    private Integer holidayAverage;

    /**
     * 租金明细
     */
    private List<RenterOrderCostDetailEntity> details ;

}
