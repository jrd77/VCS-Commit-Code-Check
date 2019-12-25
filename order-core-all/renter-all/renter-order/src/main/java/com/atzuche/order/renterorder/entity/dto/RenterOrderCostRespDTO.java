package com.atzuche.order.renterorder.entity.dto;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class RenterOrderCostRespDTO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 租车费用
     */
    private Integer rentCarAmount;
    /**
     * 佣金费用
     */
    private Integer commissionAmount;
    /**
     * 基础保障费用
     */
    private Integer basicEnsureAmount;
    /**
     * 全面保障费用
     */
    private Integer comprehensiveEnsureAmount;
    /**
     * 附加驾驶人保障费用
     */
    private Integer additionalDrivingEnsureAmount;
    /**
     * 其他费用
     */
    private Integer otherAmount;
    /**
     * 平台补贴费用
     */
    private Integer platformSubsidyAmount;
    /**
     * 车主补贴费用
     */
    private Integer carOwnerSubsidyAmount;

    /**
     * 各个金额对应的明细列表
     */
    List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList;


}
