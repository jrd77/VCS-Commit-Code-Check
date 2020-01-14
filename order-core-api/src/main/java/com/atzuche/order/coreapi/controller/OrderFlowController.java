package com.atzuche.order.coreapi.controller;


import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.atzuche.order.flow.exception.OrderFlowException;
import com.atzuche.order.flow.service.OrderFlowService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequestMapping("/order/flow")
@RestController
public class OrderFlowController {

    private static final Logger logger = LoggerFactory.getLogger(OrderFlowController.class);

    @Autowired
    private OrderFlowService orderFlowService;

    @AutoDocMethod(description = "订单状态流转列表", value = "订单状态流转列表", response = OrderFlowListResponseDTO.class)
    @PostMapping("/list")
    public ResponseData selectOrderFlowList(@Valid @RequestBody  OrderFlowRequestDTO orderFlowRequestDTO , BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            OrderFlowListResponseDTO orderFlowListResponseVO = orderFlowService.selectOrderFlowListByOrderNo(orderFlowRequestDTO);
            return ResponseData.success(orderFlowListResponseVO);
        } catch (Exception e) {
            logger.info("订单状态列表异常",e);
            throw new OrderFlowException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }


    /**
     * 验证参数
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new OrderFlowException(ErrorCode.PARAMETER_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }
    }


}
