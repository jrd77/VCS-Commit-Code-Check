package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/4/1 14:13
 */
@Data
public class OrderPlatformCouponResDTO {

    /**
     * 补贴金额
     */
    private Integer subsidyAmt;

    /**
     * 优惠券信息
     */
    private OrderCouponDTO platformCoupon;

    /**
     * 补贴明细
     */
    private RenterOrderSubsidyDetailDTO subsidyDetail;
}
