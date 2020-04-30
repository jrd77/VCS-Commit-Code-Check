package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderConsoleController {
    @Autowired
    private OrderStatusService orderStatusService;

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 17:13
     * @Description: 更具订单号查询订单状态
     *
     **/
    @RequestMapping("/order/orderStatus/queryByOrderNo")
    public ResponseData<OrderStatusDTO> getByOrderNo(@RequestParam("orderNo")String orderNo){
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = null;
        if(orderStatusEntity != null){
            orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);
        }
        return ResponseData.success(orderStatusDTO);
    }
}
