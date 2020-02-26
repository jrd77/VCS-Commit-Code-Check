package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

import java.util.List;

/**
 * 车主券返回信息
 *
 * @author pengcheng.fu
 * @date 2019/12/25 15:17
 */
@Data
public class OwnerCouponListData {

    private List<OwnerDiscountCouponVO> ownerCouponDTOList;


}
