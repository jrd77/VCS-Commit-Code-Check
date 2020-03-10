package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CostStatisticsDTO {
    @AutoDocProperty("应收")
    public int shouldReceiveAmt;

    @AutoDocProperty("实收")
    public int realReceiveAmt;
    @AutoDocProperty("免押")
    public int freeAmt;

    @AutoDocProperty("应退")
    public int shouldRetreatAmt;
    @AutoDocProperty("应扣")
    public int shouldDeductionAmt;

    @AutoDocProperty("实退")
    public int realRetreatAmt;
    @AutoDocProperty("实扣")
    public int realDeductionAmt;

}
