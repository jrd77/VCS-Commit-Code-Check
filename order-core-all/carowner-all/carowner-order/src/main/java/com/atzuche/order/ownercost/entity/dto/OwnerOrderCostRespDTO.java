package com.atzuche.order.ownercost.entity.dto;

import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class OwnerOrderCostRespDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String ownerOrderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 增值费用
     */
    private Integer incrementAmount;
    /**
     * 补贴费用
     */
    private Integer subsidyAmount;
    /**
     * 采购费用(代收代付的租金)
     */
    private Integer purchaseAmount;

    /**
     * 补贴费用明细列表
     */
    List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntityList;

    /**
     * 采购费用(代收代付的租金)明细列表
     */
    List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailEntityList;


}
