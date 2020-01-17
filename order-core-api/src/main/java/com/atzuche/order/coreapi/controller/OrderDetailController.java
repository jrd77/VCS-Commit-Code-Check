package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.ownerOrderDetail.AdminOwnerOrderDetailDTO;
import com.atzuche.order.coreapi.service.OrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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


    @PostMapping("/orderAccountDetail")
    public ResponseData<OrderAccountDetailRespDTO> orderAccountDetail(@Valid @RequestBody OrderDetailReqDTO orderDetailReqDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<OrderAccountDetailRespDTO> respData = orderDetailService.orderAccountDetail(orderDetailReqDTO);
        return respData;
    }
    @PostMapping("/status")
    public ResponseData<OrderStatusRespDTO> orderStatus(@Valid @RequestBody OrderDetailReqDTO orderDetailReqDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<OrderStatusRespDTO> respData = orderDetailService.orderStatus(orderDetailReqDTO);
        return respData;
    }
    @PostMapping("/childHistory")
    public ResponseData<OrderHistoryRespDTO> orderHistory(@Valid @RequestBody OrderHistoryReqDTO orderHistoryReqDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<OrderHistoryRespDTO> respData = orderDetailService.orderHistory(orderHistoryReqDTO);
        return respData;
    }
    @GetMapping("/adminOwnerOrderDetail")
    public ResponseData<AdminOwnerOrderDetailDTO> adminOwnerOrderDetail(@RequestParam("ownerOrderNo") String ownerOrderNo,@RequestParam("orderNo")String orderNo){
        if(ownerOrderNo==null ||ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResMsg("车主自订单号不能为空");
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            return responseData;
        }
        ResponseData<AdminOwnerOrderDetailDTO> responseData = orderDetailService.adminOwnerOrderDetail(ownerOrderNo,orderNo);
        return responseData;
    }
    @GetMapping("/dispatchHistory")
    public ResponseData<List<OrderHistoryDTO>> dispatchHistory(@RequestParam("orderNo") String orderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("订单号不能为空");
            return responseData;
        }
        ResponseData<List<OrderHistoryDTO>> responseData = orderDetailService.dispatchHistory(orderNo);
        return responseData;
    }

}
