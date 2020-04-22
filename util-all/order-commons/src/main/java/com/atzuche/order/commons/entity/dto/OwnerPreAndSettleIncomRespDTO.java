package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerPreAndSettleIncomRespDTO {
    @AutoDocProperty(value = "车主预计收益/结算后并且审核通过收益")
    private Integer ownerIncomAmt;

    //@AutoDocProperty(value = "结算后待审核收益")
   //private Integer auditingIncomAmt;

    //@AutoDocProperty(value = "车主总收益（当前会员号下的收益）")
    //private Integer ownerTotalAmt;

    @AutoDocProperty(value = "结算状态0：未结算，1:已结算，2：结算失败")
    private Integer settleStatus;

}
