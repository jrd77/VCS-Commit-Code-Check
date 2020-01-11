package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderUpdateService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderService renterOrderService;

    public ResponseData<?> rentCityAndRiskAccident(RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(rentCityAndRiskAccidentReqDTO.getOrderNo());
        orderEntity.setRentCity(rentCityAndRiskAccidentReqDTO.getRentCity());
        orderService.updateOrderByOrderNo(orderEntity);

        return null;
    }
}
