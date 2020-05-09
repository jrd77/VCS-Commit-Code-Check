package com.atzuche.order.rentercost.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author pengcheng.fu
 * @date 2020/4/13 16:46
 */

@Data
public class OwnerCouponLongVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 折扣的文案信息
     */
    private String discountDesc;
    /**
     * 租金单价折扣后的集合
     */
    private List<HolidayAverageResultVO> ownerUnitPriceRespVOS;

}
