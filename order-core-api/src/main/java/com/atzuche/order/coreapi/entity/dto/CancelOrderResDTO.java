package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * 取消订单返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/7 17:17
 */

@Data
public class CancelOrderResDTO {

    /**
     * 是否退款平台优惠券
     */
    private Boolean isReturnDisCoupon;


}
