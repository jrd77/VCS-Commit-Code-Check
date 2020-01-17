package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class FienAmtDetailDTO {
    @AutoDocProperty("车主罚金")
    private Integer ownerFienAmt;
    @AutoDocProperty("车主取还车违约金")
    private Integer ownerGetReturnCarFienAmt;
    @AutoDocProperty("车主取还车违约金编码")
    private String ownerGetReturnCarFienCashNo;
    @AutoDocProperty("车主修改地址费用")
    private Integer ownerModifyAddrAmt;
    @AutoDocProperty("车主取还车违约金编码")
    private String ownerModifyAddrAmtCashNo;
    @AutoDocProperty("租客提前还车罚金")
    private Integer renterAdvanceReturnCarFienAmt;
    @AutoDocProperty("租客延迟还车罚金")
    private Integer renterDelayReturnCarFienAmt;
}
