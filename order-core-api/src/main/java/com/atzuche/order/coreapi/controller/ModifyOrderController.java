package com.atzuche.order.coreapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.service.ModifyOrderService;
import com.autoyol.commons.web.ResponseData;

@RestController
public class ModifyOrderController {

	@Autowired
	private ModifyOrderService modifyOrderService;
	@PostMapping("/order/modify")
	public ResponseData<?> modifyOrder(@RequestBody ModifyOrderReq modifyOrderReq, BindingResult bindingResult) {
		return modifyOrderService.modifyOrder(modifyOrderReq);
	}
}
