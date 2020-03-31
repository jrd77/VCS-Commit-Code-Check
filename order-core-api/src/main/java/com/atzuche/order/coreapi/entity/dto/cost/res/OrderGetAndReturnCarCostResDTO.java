package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 取还车服务费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 16:04
 */
@Data
public class OrderGetAndReturnCarCostResDTO {

    /**
     * 取车服务费用
     */
    private Integer getCarCost;

    /**
     * 还车服务费
     */
    private Integer returnCarCost;

    /**
     * 租客取还车费用明细列表
     */
    private List<RenterOrderCostDetailEntity> details;

    /**
     * 租客补贴明细列表
     */
    private List<RenterOrderSubsidyDetailDTO> subsidyDetails;
}
