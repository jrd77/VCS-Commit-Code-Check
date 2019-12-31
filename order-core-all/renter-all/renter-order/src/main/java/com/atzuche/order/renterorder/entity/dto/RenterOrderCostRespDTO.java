package com.atzuche.order.renterorder.entity.dto;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
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
     * 租金
     */
    private Integer rentAmount;
    /**
     * 租车费用
     */
    private Integer rentCarAmount;
    /**
     * 佣金费用(手续费)
     */
    private Integer commissionAmount;
    /**
     * 基础保障费用(保险费)
     */
    private Integer basicEnsureAmount;
    /**
     * 全面保障费用(不计免赔)
     */
    private Integer comprehensiveEnsureAmount;
    /**
     * 附加驾驶人保障费用
     */
    private Integer additionalDrivingEnsureAmount;

    /**
     * 其他费用
     */
    //private Integer otherAmount;
    /**
     * 平台补贴费用
     */
    //private Integer platformSubsidyAmount;
    /**
     * 车主补贴费用
     */
    //private Integer carOwnerSubsidyAmount;
    /**
     *取还车-取车真实费用
     */
    private Integer getRealAmt;
    /**
     *取还车-还车真实费用
     */
    private Integer returnRealAmt;

    /**
     *超运能-取车费用
     */
    private Integer getOverAmt;
    /**
     * 超运能-还车费用
     */
    private Integer returnOverAmt;


    /**
     * 费用对应的明细列表
     */
    List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList;
    /**
     * 补贴对应的明细列表
     */
    List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList;


}
