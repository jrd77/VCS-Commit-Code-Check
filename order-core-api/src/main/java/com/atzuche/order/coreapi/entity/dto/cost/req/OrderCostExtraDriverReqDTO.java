package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

import java.util.List;

/**
 * 计算附加驾驶人保险费参数
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:46
 */
@Data
public class OrderCostExtraDriverReqDTO {

    /**
     * 附加驾驶人id列表
     */
    private List<String> driverIds;


    public OrderCostExtraDriverReqDTO() {
    }

    public OrderCostExtraDriverReqDTO(List<String> driverIds) {
        this.driverIds = driverIds;
    }
}
