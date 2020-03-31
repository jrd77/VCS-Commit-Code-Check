package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

/**
 * 基础保障费信息
 *
 * @author pengcheng.fu
 * @date 2020/3/3014:51
 */
@Data
public class OrderInsurAmtResDTO {

    /**
     * 基础保障费
     */
    private Integer insurAmt;

    /**
     * 基础保障费明细
     */
    private RenterOrderCostDetailEntity detail;


}
