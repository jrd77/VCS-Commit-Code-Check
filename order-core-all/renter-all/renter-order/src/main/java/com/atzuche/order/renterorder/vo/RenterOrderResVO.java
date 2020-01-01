package com.atzuche.order.renterorder.vo;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import lombok.Data;

/**
 * 租客订单返回信息
 *
 * @author pengcheng.fu
 * @date 2019/12/31 10:52
 */

@Data
public class RenterOrderResVO {

    /**
     * 车主券信息
     */
    private OrderCouponDTO ownerCoupon;

    /**
     * 租金补贴信息
     */
    private RenterOrderCostDetailEntity rentAmtEntity;

    /**
     * 车辆押金
     */
    private RenterOrderCarDepositResVO renterOrderCarDepositResVO;

    /**
     * 违章押金
     */
    private RenterOrderIllegalResVO renterOrderIllegalResVO;


    /**
     * 优惠券、凹凸币返回信息(用于绑定券、扣除币等)
     */
    private CouponAndAutoCoinResVO couponAndAutoCoinResVO;

}
