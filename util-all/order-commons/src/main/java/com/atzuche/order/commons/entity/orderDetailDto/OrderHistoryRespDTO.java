package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

@Data
public class OrderHistoryRespDTO {
    public OrderDTO orderDTO;
    /**
     * 租客历史订单(包含生效的租客订单)
     */
    public List<RenterOrderDTO> renterOrderDTOHistoryList;
    /**
     * 车主历史订单（包含生效的车主订单）
     */
    public List<OwnerOrderDTO> ownerOrderDTOHistoryLIst;

}
