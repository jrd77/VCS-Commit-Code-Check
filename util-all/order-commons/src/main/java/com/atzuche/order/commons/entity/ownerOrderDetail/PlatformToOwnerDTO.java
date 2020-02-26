package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class PlatformToOwnerDTO {
    @AutoDocProperty("油费")
    private String oliAmt;
    @AutoDocProperty("超时费用")
    private String timeOut;
    @AutoDocProperty("临时修改订单的时间和地址")
    private String modifyOrderTimeAndAddrAmt;
    @AutoDocProperty("车辆清洗费")
    private String carWash;
    @AutoDocProperty("延误等待费")
    private String dlayWait;
    @AutoDocProperty("停车费")
    private String stopCar;
    @AutoDocProperty("超里程费用")
    private String extraMileage;
}
