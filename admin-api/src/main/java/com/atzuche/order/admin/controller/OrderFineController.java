package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.order.OrderFineRequestVO;
import com.atzuche.order.admin.vo.req.order.OrderResponsibilityRequestVO;
import com.atzuche.order.admin.vo.resp.order.OrderFineResponseVO;
import com.atzuche.order.admin.vo.resp.order.OrderResponsibilityResponseVO;
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
public class OrderFineController {

    private static final Logger logger = LoggerFactory.getLogger(OrderFineController.class);


	@AutoDocMethod(description = "违约金信息", value = "违约金信息", response = OrderFineResponseVO.class)
	@GetMapping("fine/information")
	public ResponseData fineInformation(OrderFineRequestVO orderFineRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(null);
	}




}
