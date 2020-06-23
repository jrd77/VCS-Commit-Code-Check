package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="order-center-api",url = "http://localhost:1412")
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
    /*
     * @Author ZhangBin
     * @Date 2020/6/23 10:41
     * @Description: 获取有效额租客子订单
     *
     **/
    @RequestMapping("/order/renterOrder/queryRenterOrderByOrderNo")
    ResponseData<RenterOrderDTO> queryRenterOrderByOrderNo(@RequestParam("orderNo")String orderNo);

}
