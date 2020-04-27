package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

/**
 * 计算凹凸币抵扣信息参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:28
 */
@Data
public class OrderCostAutoCoinReqDTO {

    /**
     * 是否使用凹凸币:0,未使用 1,已使用
     */
    private Integer useAutoCoin;
}
