package com.atzuche.order.renterorder.entity.dto;

import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 租客订单抵扣和补贴计算公共参数抽取
 *
 * @author pengcheng.fu
 * @date 2019/12/30 16:13
 */

@Data
public class DeductAndSubsidyContextDTO {

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 租客会员注册号
     */
    private String memNo;

    /**
     * 原始租金
     */
    private int originalRentAmt;

    /**
     * 剩余租金
     */
    private int surplusRentAmt;

    /**
     * 取车服务费(取送服务券包含超运能溢价)
     */
    private int srvGetCost;

    /**
     * 还车服务费(取送服务券包含超运能溢价)
     */
    private int srvReturnCost;

    /**
     * 优惠券列表
     */
   private  List<OrderCouponDTO> orderCouponList;

    /**
     * 补贴列表
     */
   private List<RenterOrderSubsidyDetailDTO> orderSubsidyDetailList;


}
