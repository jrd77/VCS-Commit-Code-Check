package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.util.List;

@Data
public class OrderHistoryRespDTO {

    List<RenterOrderDTO> renterOrderDTOList;

    List<OwnerOrderDTO> ownerOrderDTOLIst;

}
