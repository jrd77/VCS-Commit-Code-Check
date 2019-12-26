package com.atzuche.order.renterorder.dto.coupon.owner;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2019/12/25 14:31
 */

@Data
public class OwnerCouponGetAndValidDTO implements Serializable {

    private static final long serialVersionUID = 333224633585627309L;

    private OwnerDiscountCouponDTO couponDTO;

}
