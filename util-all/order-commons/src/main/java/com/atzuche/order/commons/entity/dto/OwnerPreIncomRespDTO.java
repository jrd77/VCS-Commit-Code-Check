package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerPreIncomRespDTO {
    @AutoDocProperty(value = "车主收益")
    private Integer ownerCostAmtFinal;

}
