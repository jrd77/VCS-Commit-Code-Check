package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerPreIncomRespDTO {
    @AutoDocProperty(value = "车预计收益")
    private Integer ownerCostAmtFinal;


    @AutoDocProperty(value = "结算状态0：未结算，1:已结算，2：结算失败")
    private Integer settleStatus;

    @AutoDocProperty(value = "结算收益审核状态1,待审核 2,审核通过 3,审核拒绝")
    private Integer auditStatus;

}
