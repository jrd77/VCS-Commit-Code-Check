package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

@Data
public class ProcessRespDTO {
    private List<OrderStatusDTO> orderStatusDTOs;
    private List<OrderDTO> orderDTOs;
}
