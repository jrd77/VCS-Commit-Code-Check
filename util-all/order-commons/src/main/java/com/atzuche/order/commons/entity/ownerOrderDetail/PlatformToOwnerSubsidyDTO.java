package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class PlatformToOwnerSubsidyDTO {
    @AutoDocProperty("超里程费用")
    private Integer mileageAmt;
    @AutoDocProperty("油费补贴")
    private Integer oilSubsidyAmt;
    @AutoDocProperty("洗车费补贴")
    private Integer washCarSubsidyAmt;
    @AutoDocProperty("车上物品损失补贴")
    private Integer CarGoodsLossSubsidyAmt;
    @AutoDocProperty("延时补贴")
    private Integer delaySubsidyAmt;
    @AutoDocProperty("交通补贴")
    private Integer trafficSubsidyAmt;
    @AutoDocProperty("收益补贴")
    private Integer incomeSubsidyAmt;
    @AutoDocProperty("其他补贴")
    private Integer otherSubsidyAmt;
}
