package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

/**
 * 计算限时红包抵扣信息参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:27
 */
@Data
public class OrderCostLimitRedReqDTO {

    /**
     * 限时后保抵扣金额
     */
    private Integer reductiAmt;


    public OrderCostLimitRedReqDTO() {
    }

    public OrderCostLimitRedReqDTO(Integer reductiAmt) {
        this.reductiAmt = reductiAmt;
    }
}
