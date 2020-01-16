package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class ServiceDetailDTO {
    @AutoDocProperty("车辆类型")
    private Integer carType;
    @AutoDocProperty("服务费比例")
    private String serviceRate;
    @AutoDocProperty("服务费")
    private Double serviceAmt;
}
