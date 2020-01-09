package com.atzuche.order.coreapi.controller;

import com.atzuche.order.open.dto.OrderDetailReqDTO;
import com.atzuche.order.open.dto.OrderDetailRespDTO;
import com.atzuche.order.coreapi.service.OrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/order/detail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/query")
    public ResponseData<OrderDetailRespDTO> orderDetail(@Valid @RequestBody OrderDetailReqDTO orderDetailReqDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<OrderDetailRespDTO> respData = orderDetailService.orderDetail(orderDetailReqDTO);
        return respData;
    }

}
