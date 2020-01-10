package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.autocoin.AutoCoinRequestVO;
import com.atzuche.order.admin.vo.req.order.MainOrderRequestVO;
import com.atzuche.order.admin.vo.resp.autocoin.AutoCoinResponseVO;
import com.atzuche.order.admin.vo.resp.order.MainOrderResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class MainOrderController {

    private static final Logger logger = LoggerFactory.getLogger(MainOrderController.class);


	@AutoDocMethod(description = "获取主订单信息", value = "获取主订单信息", response = MainOrderResponseVO.class)
	@GetMapping("main/information")
	public ResponseData mainInformation(MainOrderRequestVO mainOrderRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(null);
	}




}
