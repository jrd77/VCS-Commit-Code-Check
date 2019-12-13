package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.entity.request.SubmitReq;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseData submitOrder(@RequestBody SubmitReq submitReqDto){
        ResponseData responseData = submitOrderService.submitOrder(submitReqDto);
        return responseData;
    }

}
