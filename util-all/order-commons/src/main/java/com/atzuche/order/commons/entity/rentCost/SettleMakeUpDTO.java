package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class SettleMakeUpDTO {
    @AutoDocProperty("应收")
    public int shouldReveiveAmt = 0;
    @AutoDocProperty("实收")
    public int realReveiveAmt = 0;
}
