package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

/**
 * 计算车主券抵扣信息参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:24
 */
@Data
public class OrderCostOwnerCouponReqDTO {


    /**
     * 车主券编码
     */
    private String couponNo;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 备注
     */
    private Integer mark;

}
