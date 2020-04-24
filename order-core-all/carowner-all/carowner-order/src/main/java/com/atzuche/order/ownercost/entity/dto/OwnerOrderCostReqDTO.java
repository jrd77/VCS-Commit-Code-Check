package com.atzuche.order.ownercost.entity.dto;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
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
     * 平台服务费比例（仅车主端有）
     */
    private Double serviceRate;

    /**
     * 代管车服务费比例（仅车主端有）
     */
    private Double serviceProxyRate;
    /**
     * GPS序号
     */
    private String gpsSerialNumber;
    /**
     * 租客端费用对应的明细
     */
    private OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity;
    /**
     * 租客端补贴对应的明细
     */
    private List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetails;


}
