package com.atzuche.order.renterorder.vo.owner;

import java.util.List;

import com.atzuche.order.rentercost.entity.vo.HolidayAverageResultVO;

import lombok.Data;

/**
 * 长租折扣券信息
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:03
 */
@Data
public class OwnerCouponLongResVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 折扣的文案信息
     */
    private String discountDesc;
    /**
     * 租金单价折扣后的集合
     */
    private List<HolidayAverageResultVO> ownerUnitPriceRespVOS;

}
