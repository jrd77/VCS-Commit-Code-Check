package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.dto.SubmitReqDto;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/submitOrder")
@RestController
public class SubmitOrderController {
    @Autowired
    private SubmitOrderService submitOrderService;

    /*
     * @Author ZhangBin
     * @Date 2019/12/12 15:36
     * @Description: 短租订单 app提交
     *
     **/
    @PostMapping("/")
    public void submitOrder(@RequestBody SubmitReqDto submitReqDto){

        submitOrderService.submitOrder(submitReqDto);

    }

}
