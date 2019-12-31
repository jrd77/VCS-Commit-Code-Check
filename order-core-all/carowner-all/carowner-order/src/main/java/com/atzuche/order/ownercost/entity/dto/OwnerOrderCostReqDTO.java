package com.atzuche.order.ownercost.entity.dto;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class OwnerOrderCostReqDTO {
    /**
     * 基础数据
     */
    private CostBaseDTO costBaseDTO;
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
     * 租客端费用对应的明细列表
     */
    List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList;
    /**
     * 租客端补贴对应的明细列表
     */
    List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOList;


}
