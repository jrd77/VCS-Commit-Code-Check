package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.request.OrderInsuranceRequestVO;
import com.atzuche.order.admin.vo.response.OrderInsuranceResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/order/insurance")
@RestController
@AutoDocVersion(version = "购买保险接口文档")
public class OrderInsuranceController {

	@AutoDocMethod(description = "购买保险列表", value = "购买保险列表", response = OrderInsuranceResponseVO.class)
	@PostMapping("/list")
	public ResponseData<?> list(@RequestBody OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
	}
}
