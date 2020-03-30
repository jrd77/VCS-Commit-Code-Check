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

    private Integer rentAmt;


    List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities;

}
