package com.atzuche.order.coreapi.controller;

import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供订单中租客相关信息的接口
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/12 9:40 上午
 **/
@RestController
public class RenterInfoController {

    @Autowired
    private OrderService orderService;

    public void getRenterInfo(String orderNo){
        OrderEntity orderEntity= orderService.getOrderEntity(orderNo);

    }
}
