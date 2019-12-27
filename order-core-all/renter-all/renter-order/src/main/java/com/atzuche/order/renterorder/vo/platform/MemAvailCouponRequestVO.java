package com.atzuche.order.renterorder.vo.platform;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取租客有效平台优惠券请求参数
 *
 * @author pengcheng.fu
 * @date 2019/12/26 14:41
 */
@Data
public class MemAvailCouponRequestVO implements Serializable {

    private static final long serialVersionUID = -5149532041298618909L;
    /**
     * 优惠券ID(平台券+取送服务券)
     */
    private String disCouponId;

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
