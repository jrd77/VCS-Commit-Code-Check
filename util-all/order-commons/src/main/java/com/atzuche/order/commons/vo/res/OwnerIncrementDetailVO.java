package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerIncrementDetailVO {
    @AutoDocProperty(value = "GPS服务费")
    private Integer gpsServiceAmt;

    @AutoDocProperty(value = "平台服务费")
    private Integer serviceCharge;

    @AutoDocProperty(value = "车主取车服务费")
    private Integer srvGetCostOwner;

    @AutoDocProperty(value = "车主还车服务费")
    private Integer srvReturnCostOwner;

}

