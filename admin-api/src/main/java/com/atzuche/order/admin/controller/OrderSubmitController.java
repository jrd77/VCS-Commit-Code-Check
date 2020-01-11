package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.OrderSubmitService;
import com.atzuche.order.admin.vo.req.orderSubmit.AdminTransReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/console/order")
@RestController
@AutoDocVersion(version = "下单")
public class OrderSubmitController {
    @Autowired
    private OrderSubmitService orderSubmitService;

    @AutoDocMethod(description = "下单", value = "下单", response = OrderResVO.class)
    @PostMapping("/submit")
    public ResponseData<OrderResVO> submit(@Valid @RequestBody AdminTransReqVO adminOrderReqVO, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<OrderResVO> responseData = orderSubmitService.submit(adminOrderReqVO,request);
        return responseData;
    }


}
