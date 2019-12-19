package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.atzuche.order.request.NormalOrderReqVO;
import com.atzuche.order.response.NormalOrderRespVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/order")
@RestController
public class SubmitOrderController {
    @Autowired
    private SubmitOrderService submitOrderService;

    @PostMapping("/req")
    public ResponseData<NormalOrderRespVO> submitOrder(@RequestBody NormalOrderReqVO submitReqDto){
        return null;
    }

}
