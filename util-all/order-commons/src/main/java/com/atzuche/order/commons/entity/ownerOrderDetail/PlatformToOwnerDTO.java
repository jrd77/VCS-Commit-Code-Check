package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class PlatformToOwnerDTO {
    @AutoDocProperty("油费")
    private Integer oliAmt;
    @AutoDocProperty("超时费用")
    private Integer timeOut;
    @AutoDocProperty("临时修改订单的时间和地址")
    private Integer modifyOrderTimeAndAddrAmt;
    @AutoDocProperty("车辆清洗费")
    private Integer carWash;
    @AutoDocProperty("延误等待费")
    private Integer dlayWait;
    @AutoDocProperty("停车费")
    private Integer stopCar;
    @AutoDocProperty("超里程费用")
    private Integer extraMileage;
}
