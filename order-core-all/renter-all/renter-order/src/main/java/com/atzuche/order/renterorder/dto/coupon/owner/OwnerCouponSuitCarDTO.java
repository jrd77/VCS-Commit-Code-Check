package com.atzuche.order.renterorder.dto.coupon.owner;

import lombok.Data;

import java.io.Serializable;

/**
 * 车主优惠券适合车辆信息
 *
 * @author pengcheng.fu
 * @date 2019/12/25 14:25
 */

@Data
public class OwnerCouponSuitCarDTO implements Serializable {

    private static final long serialVersionUID = 6479368910546774667L;

    private Long carNo;

    private String plateNum;


}
