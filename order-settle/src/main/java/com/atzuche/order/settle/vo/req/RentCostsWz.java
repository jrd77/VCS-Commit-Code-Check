package com.atzuche.order.settle.vo.req;

import java.util.List;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;

import lombok.Data;

/**
 * 租客费用信息
 */
@Data
public class RentCostsWz {
    /**
     * 查询租车费用(违章)
     */
    private List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails;

}
