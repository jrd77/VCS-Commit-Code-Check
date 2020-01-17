package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderHistoryListDTO {


    @AutoDocProperty("历史订单列表")
    private List<OrderHistoryDTO> OrderHistoryList ;
}
