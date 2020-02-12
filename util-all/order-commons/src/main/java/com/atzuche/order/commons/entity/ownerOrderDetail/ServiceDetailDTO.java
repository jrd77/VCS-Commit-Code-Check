package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ServiceDetailDTO {
    @AutoDocProperty("车辆类型")
    private String carType;
    @AutoDocProperty("服务费比例")
    private Double serviceRate;
    @AutoDocProperty("服务费")
    private Integer serviceAmt;
}
