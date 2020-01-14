package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderHistoryRespDTO {
    /**
     * 主订单
     */
    @AutoDocProperty("主订单")
    public OrderDTO orderDTO;
    /**
     * 车主详情
     */
    @AutoDocProperty("车主订单列表")
    public List<OwnerDetailDTO> ownerDetailDTOS;
    /**
     * 租客详情
     */
    @AutoDocProperty("租客订单列表")
    public List<RenterDetailDTO> renterDetailDTOS;

}
