package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class RentCarCostDTO {
    @AutoDocProperty("费用合计")
    public CostStatisticsDTO costStatisticsDTO;

    @AutoDocProperty("基础费用")
    public BaseCostDTO baseCostDTO;
    @AutoDocProperty("优惠券抵扣")
    public CouponDeductionDTO couponDeductionDTO;
    @AutoDocProperty("平台补贴")
    public PlatformSubsidyDTO platformSubsidyDTO;
    @AutoDocProperty("车主给租客补贴-租客租金补贴")
    public Integer rentAmtSubsidy = 0;
}
