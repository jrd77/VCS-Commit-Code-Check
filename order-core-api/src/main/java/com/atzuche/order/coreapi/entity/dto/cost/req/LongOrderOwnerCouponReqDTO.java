package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

/**
 * 计算长租订单抵扣租金参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 15:01
 */
@Data
public class LongOrderOwnerCouponReqDTO {

    /**
     * 车辆注册号
     */
    private String carNo;

    /**
     * 车主券编码
     */
    private String couponCode;
}
