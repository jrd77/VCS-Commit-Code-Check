package com.atzuche.order.coreapi.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.coreapi.entity.request.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.entity.request.ModifyOrderAppReq;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.service.ModifyOrderOwnerConfirmService;
import com.atzuche.order.coreapi.service.ModifyOrderService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ModifyOrderController {

	@Autowired
	private ModifyOrderService modifyOrderService;
	@Autowired
	private ModifyOrderOwnerConfirmService modifyOrderOwnerConfirmService;
	
	/**
	 * 修改订单（APP端或H5端）
	 * @param modifyOrderAppReq
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/modify")
	public ResponseData<?> modifyOrder(@Valid @RequestBody ModifyOrderAppReq modifyOrderAppReq, BindingResult bindingResult) {
		log.info("修改订单（APP端或H5端）modifyOrderAppReq=[{}]", modifyOrderAppReq);
		if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
		// 属性拷贝
		ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
		BeanUtils.copyProperties(modifyOrderAppReq, modifyOrderReq);
		return modifyOrderService.modifyOrder(modifyOrderReq);
	}
	
	/**
	 * 修改订单（管理后台）
	 * @param modifyOrderReq
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/modifyconsole")
	public ResponseData<?> modifyOrderForConsole(@Valid @RequestBody ModifyOrderReq modifyOrderReq, BindingResult bindingResult) {
		log.info("修改订单（管理后台）modifyOrderReq=[{}] ", modifyOrderReq);
		if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
		// 设置为管理后台修改
		modifyOrderReq.setConsoleFlag(true);
		return modifyOrderService.modifyOrder(modifyOrderReq);
	}
	
	/**
	 * 车主处理修改申请
	 * @param modifyApplyHandleReq
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/modifyconfirm")
	public ResponseData<?> ownerHandleModifyApplication(@Valid @RequestBody ModifyApplyHandleReq modifyApplyHandleReq, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
		return modifyOrderOwnerConfirmService.modifyConfirm(modifyApplyHandleReq);
	}
}
