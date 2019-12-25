package com.atzuche.order.renterorder.dto.coupon;

import lombok.Data;

/**
 * 获取并校验车主券请求参数封装
 *
 * @author pengcheng.fu
 * @date 2019/12/2516:11
 */
@Data
public class OwnerCouponGetAndValidReqDTO {

    private String orderNo;

    private Integer rentAmt;

    private String couponNo;

    private Integer carNo;

    private Integer mark;


}
