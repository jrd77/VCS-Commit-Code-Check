package com.atzuche.order.coreapi.entity.dto.cost;

import lombok.Data;

/**
 * 订单费用项信息
 *
 * @author pengcheng.fu
 * @date 2020/4/216:06
 */

@Data
public class OrderCostDTO {

    /**
     * 费用项是否存在
     */
    private Boolean isExist;

    /**
     * 费用项金额
     */
    private Integer amt;

    public OrderCostDTO() {
        
    }

    public OrderCostDTO(Boolean isExist, Integer amt) {
        this.isExist = isExist;
        this.amt = amt;
    }
}
