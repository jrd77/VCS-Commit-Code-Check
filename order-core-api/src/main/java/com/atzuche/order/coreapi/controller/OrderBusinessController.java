package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.atzuche.order.coreapi.service.OrderBusinessService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class OrderBusinessController {
    @Autowired
    private OrderBusinessService orderBusinessService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/orderBusiness/renterAndOwnerSeeOrder")
    public ResponseData<?> renterAndOwnerSeeOrder(@RequestBody @Valid RenterAndOwnerSeeOrderVO renterAndOwnerSeeOrderVO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        OrderEntity orderEntity = orderService.getOrderEntity(renterAndOwnerSeeOrderVO.getOrderNo());
        if(orderEntity == null){
            log.error("订单号不存在orderNo={}",renterAndOwnerSeeOrderVO.getOrderNo());
            return ResponseData.error();
        }
        orderBusinessService.renterAndOwnerSeeOrder(renterAndOwnerSeeOrderVO);
        return ResponseData.success();
    }
}
