package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderNoListDTO {
    @AutoDocProperty(value = "主订单号")
    private List<String> orderNo;
}
