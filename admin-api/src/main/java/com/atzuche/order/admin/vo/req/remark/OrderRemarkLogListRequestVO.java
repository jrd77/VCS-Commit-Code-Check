package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkLogListRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;



}
