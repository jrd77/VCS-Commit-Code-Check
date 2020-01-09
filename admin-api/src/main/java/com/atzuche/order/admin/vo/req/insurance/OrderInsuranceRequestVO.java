package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInsuranceRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;
}
