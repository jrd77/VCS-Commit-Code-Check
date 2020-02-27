package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerFineVO {
    @AutoDocProperty("修改订单取车违约金")
    private Integer modifyGetFine;
    @AutoDocProperty(value = "修改订单还车违约金")
    private Integer modifyReturnFine;
    @AutoDocProperty(value = "修改订单提前还车违约金")
    private Integer modifyAdvance;
    @AutoDocProperty(value = "取消订单违约金")
    private Integer cancelFine;
    @AutoDocProperty(value = "延迟还车违约金")
    private Integer delayFine;
    @AutoDocProperty(value = "车主修改交接车地址罚金")
    private Integer modifyAddressFine;
    @AutoDocProperty(value = "租客提前还车罚金")
    private Integer renterAdvanceReturn;
    @AutoDocProperty(value = "租客延迟还车罚金")
    private Integer renterDelayReturn;
    @AutoDocProperty(value = "车主违约罚金")
    private Integer ownerFine;
    @AutoDocProperty(value = "取还车违约金")
    private Integer getReturnCar;
}

