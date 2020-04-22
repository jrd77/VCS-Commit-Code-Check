package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerPreAndSettleIncomRespDTO {
    @AutoDocProperty(value = "车主收益")
    private Integer ownerIncomAmt;

    @AutoDocProperty(value = "结算状态0：未结算，1:已结算，2：结算失败")
    private Integer settleStatus;

}
