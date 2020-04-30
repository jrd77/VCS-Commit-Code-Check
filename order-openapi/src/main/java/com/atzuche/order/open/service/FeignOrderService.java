package com.atzuche.order.open.service;

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
     * @Description: 更具订单号查询订单状态
     *
     **/
    @RequestMapping("/order/orderStatus/queryByOrderNo")
    public ResponseData<OrderStatusDTO> getByOrderNo(@RequestParam("orderNo")String orderNo);


}
