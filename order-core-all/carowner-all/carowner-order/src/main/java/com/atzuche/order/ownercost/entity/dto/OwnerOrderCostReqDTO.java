package com.atzuche.order.ownercost.entity.dto;

import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class OwnerOrderCostReqDTO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 车主会员号
     */
    private String memNo;

    /**
     * 租客端费用对应的明细列表
     */
    List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList;
    /**
     * 租客端补贴对应的明细列表
     */
    List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOList;


}
