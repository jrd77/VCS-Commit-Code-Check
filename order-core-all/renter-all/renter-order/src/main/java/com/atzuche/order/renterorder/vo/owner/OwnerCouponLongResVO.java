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

    private List<HolidayAverageResultVO> ownerUnitPriceRespVOS;

}
