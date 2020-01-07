package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.order.MainOrderRequestVO;
import com.atzuche.order.admin.vo.req.payment.PaymentRequestVO;
import com.atzuche.order.admin.vo.resp.order.MainOrderResponseVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentInformationResponseVO;
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
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);


	@AutoDocMethod(description = "支付信息", value = "支付信息", response = PaymentInformationResponseVO.class)
	@GetMapping("payment/information")
	public ResponseData platformDisCouponList(@RequestBody PaymentRequestVO paymentRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(null);
	}




}
