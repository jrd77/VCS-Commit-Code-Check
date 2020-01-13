package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

import java.io.Serializable;

/**
 * 车主券返回信息
 *
 * @author pengcheng.fu
 * @date 2019/12/25 15:17
 */
@Data
public class OwnerCouponListResult implements Serializable {

    private static final long serialVersionUID = 1849250360740519896L;

    private String resCode;

    private String resMsg;

    private OwnerCouponListData data;



}
