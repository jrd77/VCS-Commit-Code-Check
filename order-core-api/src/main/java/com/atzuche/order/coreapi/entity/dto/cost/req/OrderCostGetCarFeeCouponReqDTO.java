package com.atzuche.order.coreapi.entity.dto.cost.req;

import lombok.Data;

import java.util.List;

/**
 * 计算送取服务券抵扣信息参数
 * <p><font color = red>送取服务券</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/4/7 14:59
 */
@Data
public class OrderCostGetCarFeeCouponReqDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 取送服务券ID
     */
    private String getCarFreeCouponId;

    /**
     * 租客会员号
     */
    private Integer memNo;

    /**
     * 城市编码
     */
    private Integer cityCode;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 租期起始时间
     */
    private Long rentTime;

    /**
     * 租期截止时间
     */
    private Long revertTime;

    /**
     * 租金(抵扣后的平台优惠券使用)
     */
    private Integer rentAmt;

    /**
     * 原始租金(取送服务优惠券使用)
     */
    private Integer originalRentAmt;

    /**
     * 保险费用
     */
    private Integer insureTotalPrices;

    /**
     * 不计免赔费用
     */
    private Integer abatement;

    /**
     * 手续费用
     */
    private Integer counterFee;

    /**
     * 取车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvGetCost;

    /**
     * 还车服务费(取送服务券包含超运能溢价)
     */
    private Integer srvReturnCost;

    /**
     * 日均价(多个时取最新的)
     */
    private Integer holidayAverage;

    /**
     * 租客是否是新用户下单
     */
    private Boolean isNew;

    /**
     * 车辆标签(老标签)
     */
    private List<String> labelIds;
}
