package com.atzuche.order.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.PaymentService;
import com.atzuche.order.admin.vo.req.payment.PaymentRequestVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentInformationResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);
    @Autowired
    PaymentService paymentService;

	@AutoDocMethod(description = "支付信息", value = "支付信息", response = PaymentInformationResponseVO.class)
//	@GetMapping("payment/information")
	@RequestMapping(value="payment/information",method = RequestMethod.POST)
	public ResponseData platformPaymentList(@RequestBody @Validated PaymentRequestVO paymentRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	PaymentInformationResponseVO respVo = paymentService.platformPaymentList(paymentRequestVO);
        	return ResponseData.success(respVo);
		} catch (Exception e) {
			Cat.logError("获取支付信息异常:params="+paymentRequestVO.toString(),e);
			logger.error("获取支付信息异常:params="+paymentRequestVO.toString(),e);
			return ResponseData.error();
		}
		
	}


}
