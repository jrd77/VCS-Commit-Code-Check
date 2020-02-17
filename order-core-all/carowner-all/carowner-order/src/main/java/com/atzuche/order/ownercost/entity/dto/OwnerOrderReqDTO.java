package com.atzuche.order.ownercost.entity.dto;

import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OwnerOrderReqDTO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 显示起租时间
     */
    private LocalDateTime showRentTime;
    /**
     * 显示还车时间
     */
    private LocalDateTime showRevertTime;
    /**
     * 预计起租时间
     */
    private LocalDateTime expRentTime;
    /**
     * 预计还车时间
     */
    private LocalDateTime expRevertTime;
    /**
     * 应答标识位，0未设置，1已设置
     */
    private Integer replyFlag;
    /**
     * 是否使用特供价 0-否，1-是
     */
    private Integer isUseSpecialPrice;
    /**
     * 车辆类型
     */
    private Integer carOwnerType;
    /**
     * 取车标志
     */
    private Integer srvGetFlag;
    /**
     * 还车标志
     */
    private Integer srvReturnFlag;
    /**
     * 车辆号
     */
    private String carNo;
    /**
     * 1、短租 2、套餐
     */
    private Integer category;

    /**
     * 平台服务费比例（仅车主端有）
     */
    private Double serviceRate;

    /**
     * 代管车服务费比例（仅车主端有）
     */
    private Double serviceProxyRate;

    /**
     * 补贴费用明细（车主券）
     */
    private OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity;

    /**
     * 租金明细
     */
    private OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity;
}
