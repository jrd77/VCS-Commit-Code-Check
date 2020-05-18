package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="order-center-api")
public interface FeignOrderService {

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 17:13
     * @Description: 根据订单号查询订单状态
     *
     **/
    @RequestMapping("/order/orderStatus/queryByOrderNo")
    ResponseData<OrderStatusDTO> queryOrderStatusByOrderNo(@RequestParam("orderNo")String orderNo);

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 17:13
     * @Description: 根据订单号查询订单状态
     *
     **/
    @RequestMapping("/order/parentOrder/queryByOrderNo")
    ResponseData<OrderDTO> queryOrderByOrderNo(@RequestParam("orderNo")String orderNo);
}
