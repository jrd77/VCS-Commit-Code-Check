package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "order-center-api")
public interface FeignOrderDetailService {
    /*
     * @Author ZhangBin
     * @Date 2020/1/8 21:06
     * @Description: 获取订单详情
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/orderCenter/detail")
    ResponseData<OrderDetailRespDTO> getOrderDetail(@RequestBody OrderDetailReqDTO orderDetailReqDTO);

}
