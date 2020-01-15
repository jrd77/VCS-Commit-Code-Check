package com.atzuche.order.commons.vo.res.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DistributionCostVO {
    @AutoDocProperty(value="取车费用")
    private String getAmt;
    @AutoDocProperty(value="还车费用")
    private String returnAmt;
    @AutoDocProperty(value="取车超运能加价")
    private String getOverTransportFee;
    @AutoDocProperty(value="还车超运能加价")
    private String returnOverTransportFee;
}
