package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

@Data
public class OrderHistoryRespDTO {
    /**
     * 主订单
     */
    public OrderDTO orderDTO;
    /**
     * 车主详情
     */
    public List<OwnerDetailDTO> ownerDetailDTOS;
    /**
     * 租客详情
     */
    public List<RenterDetailDTO> renterDetailDTOS;

}
