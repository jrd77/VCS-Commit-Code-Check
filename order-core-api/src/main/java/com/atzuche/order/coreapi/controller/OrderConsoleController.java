package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
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
    @Autowired
    private OrderService orderService;

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 17:13
     * @Description: 根据订单号查询订单状态
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

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 17:13
     * @Description: 根据订单号查询主订单
     *
     **/
    @RequestMapping("/order/parentOrder/queryByOrderNo")
    public ResponseData<OrderDTO> queryByOrderNo(@RequestParam("orderNo")String orderNo){
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        OrderDTO orderDTO= null;
        if(orderEntity != null){
            orderDTO = new OrderDTO();
            BeanUtils.copyProperties(orderEntity,orderDTO);
        }
        return ResponseData.success(orderDTO);
    }
}
