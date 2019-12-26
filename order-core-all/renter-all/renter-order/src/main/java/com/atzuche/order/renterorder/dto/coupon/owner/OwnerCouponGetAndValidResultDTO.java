package com.atzuche.order.renterorder.dto.coupon.owner;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pengcheng.fu
 * @date 2019/12/25 14:34
 */

@Data
public class OwnerCouponGetAndValidResultDTO implements Serializable {

    private static final long serialVersionUID = -7968521073637424630L;

    private String resCode;

    private String resMsg;

    private OwnerCouponGetAndValidDTO data;


}
