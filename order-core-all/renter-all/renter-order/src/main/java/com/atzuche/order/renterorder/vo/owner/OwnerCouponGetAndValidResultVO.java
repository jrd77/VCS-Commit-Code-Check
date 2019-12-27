package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pengcheng.fu
 * @date 2019/12/25 14:34
 */

@Data
public class OwnerCouponGetAndValidResultVO implements Serializable {

    private static final long serialVersionUID = -7968521073637424630L;

    private String resCode;

    private String resMsg;

    private OwnerCouponGetAndValidVO data;


}
